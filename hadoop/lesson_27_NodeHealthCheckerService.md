NodeHealthCheckerServic是用于检测结点健康状态的服务, 其用法如下:

需求: 检测机器的内存余量, 如果余量低于系统内存的10%, 则节点不再提供服务.

Step1: 编写Shell脚本如下
```
$ cat memory_check.sh
#!/bin/bash
MEMORY_RATIO=0.1
freeMem=`grep MemFree /proc/meminfo | awk '{print $2}'`
totalMem=`grep MemTotal /proc/meminfo | awk '{print $2}'`
limitMem=`echo | awk '{print int("'$totalMem'"*"'$MEMORY_RATIO'")}'`

if [ $freeMem -lt $limitMem  ]; then
   echo "ERROR, totalMem=$totalMem, freeMem=$freeMem, limitMem=$limitMem"
else
   echo "OK, totalMem=$totalMem, freeMem=$freeMem, limitMem=$limitMem"
fi
```
需要注意的是, 如果检测到内存余量不足, 则一定要输出杳标识"ERROR", 即
```
   echo "ERROR, totalMem=$totalMem, freeMem=$freeMem, limitMem=$limitMem"
```
原因稍后解释.

Step 2: 配置Yarn参数
```
conf.set(YarnConfiguration.NM_HEALTH_CHECK_SCRIPT_PATH, "/home/shgy/tmp/memory_check.sh");
conf.setInt(YarnConfiguration.NM_HEALTH_CHECK_INTERVAL_MS, 60*1000);
```
由于默认的CHECK_INTERVAL是`10 * 60 * 1000`即10分钟, 在测试时, 等待时间太长, 这里修改为1分钟.

Step3: 启动MiniYarnCluster

```
YarnConfiguration conf = new YarnConfiguration();
conf.setInt(YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB, 128);
conf.set(YarnConfiguration.NM_HEALTH_CHECK_SCRIPT_PATH, "/home/shgy/tmp/memory_check.sh");
conf.setInt(YarnConfiguration.NM_HEALTH_CHECK_INTERVAL_MS, 60*1000);
conf.set("yarn.log.dir", "target");
conf.setBoolean(YarnConfiguration.TIMELINE_SERVICE_ENABLED, true);
conf.set(YarnConfiguration.RM_SCHEDULER, CapacityScheduler.class.getName());
MiniYARNCluster yarnCluster = new MiniYARNCluster("testapp", 1, 1, 1, 1, true);
yarnCluster.init(conf);

yarnCluster.start();

int sec = 60;
while (sec >= 0) {
    if (yarnCluster.getResourceManager().getRMContext().getRMNodes().size() >= 1) {
        break;
    }
    Thread.sleep(1000);
    sec--;
}

 URL url = Thread.currentThread().getContextClassLoader().getResource("yarn-site.xml");
  if (url == null) {
    throw new RuntimeException("Could not find 'yarn-site.xml' dummy file in classpath");
  }
  Configuration yarnClusterConfig = yarnCluster.getConfig();
  yarnClusterConfig.set("yarn.application.classpath", new File(url.getPath()).getParent());
  //write the document to a buffer (not directly to the file, as that
  //can cause the file being written to get read -which will then fail.
  ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
  yarnClusterConfig.writeXml(bytesOut);
  bytesOut.close();
  //write the bytes to the file in the classpath
  OutputStream os = new FileOutputStream(new File(url.getPath()));
  os.write(bytesOut.toByteArray());
  os.close();
FileContext fsContext = FileContext.getLocalFSFileContext();
Path path = new Path(conf
        .get("yarn.timeline-service.leveldb-timeline-store.path"));

fsContext.delete(path,true);
try {
  Thread.sleep(2000);
} catch (InterruptedException e) {
  // LOG.info("setup thread sleep interrupted. message=" + e.getMessage());
}

```
启动成功后等待一段时间, 会在日志中输出
```
[INFO ] 2016-12-12 15:54:11,166(153310) --> [AsyncDispatcher event handler] org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeImpl.handle(RMNodeImpl.java:423): localhost:55860 Node Transitioned from RUNNING to UNHEALTHY
[INFO ] 2016-12-12 15:54:11,167(153311) --> [ResourceManager Event Processor] org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler.removeNode(CapacityScheduler.java:1181): Removed node localhost:55860 clusterResource: <memory:0, vCores:0>
```

相关的pom.xml
```
        <dependency>
		    <groupId>org.apache.hadoop</groupId>
		    <artifactId>hadoop-minicluster</artifactId>
		    <version>2.6.0</version>
		</dependency>
	   <dependency>
		    <groupId>org.apache.httpcomponents</groupId>
		    <artifactId>httpclient</artifactId>
		    <version>4.2</version>
		</dependency>
```
相关的log.properties
```
log4j.rootLogger=INFO,console,file
log4j.additivity.org.apache=true
# (console)
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.Threshold=INFO
log4j.appender.console.ImmediateFlush=true
log4j.appender.console.Target=System.err
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=[%-5p] %d(%r) --> [%t] %l: %m %x %n

# (file)
# Define the file appender
log4j.appender.file=org.apache.log4j.FileAppender
# Set the name of the file
log4j.appender.file.File=log.out
log4j.appender.file.Threshold=INFO
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=[%-5p] %d(%r) --> [%t] %l: %m %x %n
```

查看Yarn的源码, 逻辑还是比较清晰的.
在启动阶段, NodeHealthCheckerService.serviceInit()方法会去检测是否需要NodeHealthScriptRunner服务
```
  // NodeHealthCheckerService
  protected void serviceInit(Configuration conf) throws Exception {
    if (NodeHealthScriptRunner.shouldRun(conf)) {
      nodeHealthScriptRunner = new NodeHealthScriptRunner();
      addService(nodeHealthScriptRunner);
    }
    addService(dirsHandler);
    super.serviceInit(conf);
  }
```
如果设置了`YarnConfiguration.NM_HEALTH_CHECK_SCRIPT_PATH`参数, 并且参数文件存在且可执行, 就会添加
`NodeHealthScriptRunner`

在`NodeHealthScriptRunner.serviceStart()`阶段,启动一个定时服务
```
  protected void serviceStart() throws Exception {
    // if health script path is not configured don't start the thread.
    if (!shouldRun(conf)) {
      LOG.info("Not starting node health monitor");
      return;
    }
    nodeHealthScriptScheduler = new Timer("NodeHealthMonitor-Timer", true);
    // Start the timer task immediately and
    // then periodically at interval time.
    nodeHealthScriptScheduler.scheduleAtFixedRate(timer, 0, intervalTime);
    super.serviceStart();
  }
```
定时执行shell脚本