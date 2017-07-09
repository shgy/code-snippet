学习YARN， 光看代码没多久就忘记了。 就想着从distributed-shell的demo入手， 修改这个demo.
自己新建了一个module, 命名为shgy-demos, 其结构如下：
```
├── src
│   ├── main
│   │   └── java
│   │       └── shgy
│   │           └── yarn
│   │               └── app
│   │                   ├── ApplicationMaster.java
│   │                   ├── Client.java
│   │                   ├── DSConstants.java
│   │                   └── Log4jPropertyHelper.java
│   └── test
│       ├── java
│       │   └── YarnClientDemo.java
│       └── resources
│           ├── log4j.properties
│           └── yarn-site.xml

```
相关的代码都是从hadoop的源码中copy过来的。

最开始没有创建test目录， 
用shgy.yarn.app包下的Client和ApplicationMaster.
结果运行报错， AppMaster无法连接到ResourceManager.



在这个问题上卡了好久， 重新梳理了一下Yarn运行App的过程：
1. Client  ---> submit ---> App
2. RM      ---> negotiating  ---> NM
3. NM      ---> start   ----> AppMaster
4. AppMaster ---> negotiation  ---> NM
5. NM      ---> start ---> container

NodeManager从HDFS上下载相关的资源(jar包，依赖文件...) 然后构建jar包的运行环境。

执行的关键在于jar包。 于是， 我对比了`shgy.yarn.app`和
`org.apache.hadoop.yarn.applications.distributedshell` 两者打包的jar文件。
 其结构分别如下：
```
 ├── META-INF
 │   └── MANIFEST.MF
 └── org
     └── apache
         └── hadoop
             └── yarn
                 └── applications
                     └── distributedshell
                         ├── ApplicationMaster$1.class
                         ├── ApplicationMaster$2.class
                         ├── ApplicationMaster$3.class
                         ├── ApplicationMaster$4.class
                         ├── ApplicationMaster.class
                         ├── ApplicationMaster$DSEntity.class
                         ├── ApplicationMaster$DSEvent.class
                         ├── ApplicationMaster$LaunchContainerRunnable.class
                         ├── ApplicationMaster$NMCallbackHandler.class
                         ├── ApplicationMaster$RMCallbackHandler.class
                         ├── Client.class
                         ├── DSConstants.class
                         └── Log4jPropertyHelper.class
```
和
```
├── log4j.properties
├── META-INF
│   └── MANIFEST.MF
├── shgy
│   └── yarn
│       └── app
│           ├── ApplicationMaster$1.class
│           ├── ApplicationMaster$2.class
│           ├── ApplicationMaster$3.class
│           ├── ApplicationMaster$4.class
│           ├── ApplicationMaster.class
│           ├── ApplicationMaster$DSEntity.class
│           ├── ApplicationMaster$DSEvent.class
│           ├── ApplicationMaster$LaunchContainerRunnable.class
│           ├── ApplicationMaster$NMCallbackHandler.class
│           ├── ApplicationMaster$RMCallbackHandler.class
│           ├── Client.class
│           ├── DSConstants.class
│           └── Log4jPropertyHelper.class
├── YarnClientDemo.class
└── yarn-site.xml
```
这个jar包将yarn-site.xml也囊括进去了。 而yarn-site.xml中配置的是上次MiniYarnCluster启动时
的参数， 这里面就包含连接RPC的端口。 关键在于这个端口是随机的， 每次启动MiniYarnCluster几乎都不一样。

找到问题后， 新建test目录，将yarn-site.xml和log4j.properties移入test目录。结构如文章开头的那样。重新运行代码，正常。



