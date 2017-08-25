通常， 我们学习编程语言， 编写的第一个程序是Hello World. 学习Yarn， 也理所当然地去寻找所谓的Hello World.
可惜的是， Yarn的应用开发， 并没有Hello World. 最简单的例子是Distributed Shell. 其功能为在每个分配的container
中执行shell命令。 前面的学习日志中已有使用的例子。 本文记录的是改造Distributed Shell, 让其执行Java的代码， 输出
"hello world".
Hello.java的代码如下：
```
package shgy.yarn.app;

/**
 * Created by shgy on 17-7-9.
 */
public class Hello {
    public static void main(String[] args) {
        System.out.println("hello world");
//        System.out.println(System.getProperty("java.class.path"));
//        System.out.println(System.getProperty("user.dir"));
    }
}
```

为了改造distributed shell这个app, 需要做一些前期的准备工作。 首先温习一下distributed shell的执行流程。
例如,对于如下的参数
```
String[] args2 = {
        "--jar",
        APPMASTER_JAR,
        "--num_containers",
        "2",
        "--shell_command",
        "ls",
        "--master_memory",
        "512",
        "--master_vcores",
        "2",
        "--container_memory",
        "128",
        "--container_vcores",
        "1",
        "--log_properties",
        "/home/shgy/tmp/log4j.properties"
};
```
在MiniYarnCluster中执行的结果为:
```
├── YarnClientDemo-localDir-nm-0_0
│   ├── filecache
│   ├── nmPrivate
│   └── usercache
│       └── shgy
│           ├── appcache
│           └── filecache
└── YarnClientDemo-logDir-nm-0_0
    └── application_1499596237871_0001
        ├── container_1499596237871_0001_01_000001
        │   ├── AppMaster.stderr
        │   └── AppMaster.stdout
        ├── container_1499596237871_0001_01_000002
        │   ├── stderr
        │   └── stdout
        └── container_1499596237871_0001_01_000003
            ├── stderr
            └── stdout
```
即生成了3个container. 一个承载了AppMaster, 另外两个则执行相关的业务代码。输出都在stderr和stdout中。

由于Yarn的各个组件之间通过网络通信， 都有超时机制, 而且Container以进程的方式启动，
debug的方式就不能像研究其他的代码一样进行单步走。 需要通过日志来追踪代码的运行方式。
设置log4j的日志如下:
```
log4j.logger.shgy.yarn.app=debug,shgy
log4j.appender.shgy=org.apache.log4j.RollingFileAppender
log4j.appender.shgy.File=/home/shgy/tmp/shgy.log
log4j.appender.shgy.MaxFileSize=1024KB
log4j.appender.shgy.MaxBackupIndex=5
log4j.appender.shgy.Append=true
log4j.appender.shgy.layout=org.apache.log4j.PatternLayout
log4j.appender.shgy.layout.ConversionPattern=%d{ISO8601} %-5p [%t] %c{2} (%F:%M(%L)) - %m%n
```
启动的时候带上参数
```
"--log_properties",
"/home/shgy/tmp/log4j.properties"
```
这样就可以通过日志来查看变量是否符合预期。以上就是改造distributed shell的前期工作。

由于distributed shell只有两个核心类`Client`和`ApplicationMaster`，而Client负责向集群提交app,启动AppMaster,
AppMaster启动Container执行业务逻辑。 我们只需要改造`ApplicationMaster`, 具体地说， 只需要改动`LaunchContainerRunnable`类的`run`方法。
如何改动呢？
1. 设置container启动需要的资源， 就是AppMaster.jar文件， 因为Hello.class在AppMaster.jar中。
```
  // Set the local resources
      Map<String, LocalResource> localResources = new HashMap<String, LocalResource>();


      String appMasterJarPath = "AppMaster.jar";

      URL appMasterJarPathYarnUrl = null;
      Path shellDst = null;
      long appMasterJarFileLength = 0;
      long appMasterJarTimestamp = 0;
      try{
        FileSystem fs = FileSystem.get(conf);
        ApplicationId appId = appAttemptID.getApplicationId();
        String shellPathSuffix =
                "DistributedShell" + "/" + appId.toString() + "/" + appMasterJarPath;
        shellDst =
                new Path(fs.getHomeDirectory(), shellPathSuffix);
        System.out.println(shellDst.toString()+":"+fs.exists(shellDst));

        appMasterJarPathYarnUrl = ConverterUtils.getYarnUrlFromURI(
                new URI(shellDst.toString()));

        FileStatus fileStatus = fs.getFileStatus(shellDst);
        LOG.info("appMasterJarPath file length is : "+fileStatus.toString());
        appMasterJarFileLength = fileStatus.getLen();
        appMasterJarTimestamp = fileStatus.getModificationTime();
      }catch (Exception e){
        LOG.error("appMasterJarPath: ",e);
      }

      LocalResource appMasterJarRes = LocalResource.newInstance(appMasterJarPathYarnUrl,
              LocalResourceType.FILE, LocalResourceVisibility.APPLICATION,
              appMasterJarFileLength, appMasterJarTimestamp);
      localResources.put(appMasterJarPath, appMasterJarRes);
```
上面的代码基本就是参考Client类。

2. 设置AppMaster启动的环境上下文。
```
  Map<String, String> env = new HashMap<String, String>();
  StringBuilder classPathEnv = new StringBuilder(Environment.CLASSPATH.$$())
          .append(ApplicationConstants.CLASS_PATH_SEPARATOR).append("./*");
  for (String c : conf.getStrings(
          YarnConfiguration.YARN_APPLICATION_CLASSPATH,
          YarnConfiguration.DEFAULT_YARN_CROSS_PLATFORM_APPLICATION_CLASSPATH)) {
    classPathEnv.append(ApplicationConstants.CLASS_PATH_SEPARATOR);
    classPathEnv.append(c.trim());
  }
  classPathEnv.append(ApplicationConstants.CLASS_PATH_SEPARATOR).append(
          "./log4j.properties");

  // add the runtime classpath needed for tests to work
  if (conf.getBoolean(YarnConfiguration.IS_MINI_YARN_CLUSTER, false)) {
    classPathEnv.append(':');
    classPathEnv.append(System.getProperty("java.class.path"));
  }

  env.put("CLASSPATH", classPathEnv.toString());
  shellEnv.putAll(env);
```
看着代码好几行，就是设置CLASSPATH, 作为Java从业者， CLASSPATH的设置应该轻车熟路了。

3. 提交到NodeManager, 启动Container
```
      Vector<CharSequence> vargs = new Vector<CharSequence>(5);
      vargs.add(Environment.JAVA_HOME.$$() + "/bin/java");
      // Set Xmx based on am memory size
      vargs.add("-Xmx512m");
      // Set class name
      vargs.add("shgy.yarn.app.Hello");
      // Set executable command
      // Add log redirect params
      vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stdout");
      vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR + "/stderr");

      // Get final commmand
      StringBuilder command = new StringBuilder();
      for (CharSequence str : vargs) {
        command.append(str).append(" ");
      }

      List<String> commands = new ArrayList<String>();
      commands.add(command.toString());

      // Set up ContainerLaunchContext, setting local resource, environment,
      // command and token for constructor.

      // Note for tokens: Set up tokens for the container too. Today, for normal
      // shell commands, the container in distribute-shell doesn't need any
      // tokens. We are populating them mainly for NodeManagers to be able to
      // download anyfiles in the distributed file-system. The tokens are
      // otherwise also useful in cases, for e.g., when one is running a
      // "hadoop dfs" command inside the distributed shell.
      ContainerLaunchContext ctx = ContainerLaunchContext.newInstance(
        localResources, shellEnv, commands, null, allTokens.duplicate(), null);
      containerListener.addContainer(container.getId(), container);
      nmClientAsync.startContainerAsync(container, ctx);
```
到这里， 改动就大功告成了。 当然， 这里还有很多冗余的代码可以去掉， 让主线逻辑更清晰。

注： 由于没有启动HDFS， 所以Yarn使用的是LocalFileSystem. 其HomeDirectory就是启动应用的账户所在的Home。

个人感觉， Yarn的学习曲线是比较陡峭的。 
只看代码，分析各个组件的功能而没有实际的Coding也是很快就忘记了； 而写代码吧，也没有简单
的教程让人步步为营。 分析Distributed Shell, 也需要了解Yarn的工作原理和实现细节， 而且
又有官方的封装， 异步回调机制。 因此， 尽管只是简单的改造， 也颇费周折。


