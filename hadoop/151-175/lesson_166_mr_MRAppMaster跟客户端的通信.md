前面都已经介绍过MRAppMaster，为什么这里又开始了解MRAppMaster呢？

窃以为， 学习MapReduce框架，分为两部分： 
1. LocalJobRunner为核心MapReduce的数据处理流程。 通过LocalJobRunner， 可以了解到数据在整个RM过程中的流向。
   Map:    源文件--> InputSplit -> map() --> 环形缓冲区 --> Merge --> file.out 
   Reduce: shuffer --> Merge --> Sort -- reduce()  ---> part-xxx.out

2. MRAppMaster为核心的资源调度， 作业管理流程。 通过MRAppMaster, 可以了解到集群的资源是如何为Job服务的。

前面学习MRAppMaster, 没有深入， 主要是没有学习Yarn的工作原理。 正好前段时间初步学习了一遍Yarn， 正好可以
通过MRAppMaster来巩固Yarn的基础知识。

学习yarn-NM的时候， 发现状态机其实没有那么难理解。 重点是要把握主线。

在RMAppMaster上， 有Job, Task, TaskAttempt 这3个状态机。

Job 状态机的主线是： NEW --> INITED --> SETUP --> RUNNING --> COMMITTING -->  SUCCEEDED


RMAppMaster.serviceStart() 时候会创建JobImpl对象， Job状态机就创建起来了。


在Yarn的层面， 管理的是Application; 在mapreduce层面，管理的是Job.
 
再次回顾一下， 基于yarn的mapreduce, 一定会用到这3个协议：

1. ApplicationClientProtocol:              Client  --> ResourceManager  submitApp/forceKillApp/monitorApp

2. ApplicationMasterProtocol:   ApplicationMaster  --> ResourceManager  registerApp/finishApp

3. ContainerManagementProtocol: ApplicationMaster  --> NodeManager      start/stop container

Client 通过跟RM通信获取application运行的信息， 比如运行的队列，运行状态等信息。。。 但是这些对于mapreduce来说， 是不够的，
比如， 我希望看到map job运行了多少， reduce job运行了多少 。。。 这个需求mapreudce交给了MRAppMaster.

具体参看 `MRClientService.MRClientProtocolHandler.getJobReport`

这里有个问题： `MRClientService.MRClientProtocolHandler`是一个RPC Server, 而且是由MRAppMaster启动起来的， 
那么Client怎么连接上MRAppMaster的呢？

1. Client向ResourceManager获取`ApplicationReport`， 而`ApplicationReport`的字段如下:
```
ApplicationReport is a report of an application.

It includes details such as:
    
    ApplicationId of the application.
    Applications user.
    Application queue.
    Application name.
    Host on which the ApplicationMaster is running.
    RPC port of the ApplicationMaster.
    Tracking URL.
    YarnApplicationState of the application.
    Diagnostic information in case of errors.
    Start time of the application.
    Client Token of the application (if security is enabled).
```
这里面就有`RPC port`和`Host`， 有这些，Client就能连接到RPC Server上了。

所以， 对于mapreduce来说， 他它不仅跟ResourceManager通信， 还直接跟AppMaster通信。 

