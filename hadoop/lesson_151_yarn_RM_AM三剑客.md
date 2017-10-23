RM-AM交互的3个组件： 
```
AMLivelinessMonitor
ApplicationMasterLauncher
ApplicationMasterService
```
AMLivelinessMonitor 用于监控AM是否活着， 前面lesson_95已经提过。

ApplicationMasterLauncher 用与跟NM通信， 启动/停止 ApplicationMaster。 以mapreduce为例：
Client提交Job  --> RM启动AM --> AM启动Task。

1. ApplicationMasterLauncher 接受的事件： 
```
switch (event) {
    case LAUNCH:
      launch(application);
      break;
    case CLEANUP:
      cleanup(application);
    default:
      break;
    }
```
这个可谓相当清晰了。

2. ApplicationMasterLauncher的工作模式： 异步。
由独立的线程LanucherThread来处理Launch的工作， 线程之间通过队列通信。


以MapReduce为例， 再把线条拉长一点:
1. 任务在提交的时候，YarnRunner.createApplicationSubmissionContext()方法注明了AppMaster启动类，以及启动命令。 这是在Client端
2. ApplicationMasterLauncher接受到`LAUNCH`命令，调用RPC启动AppMaster, 这是在RM端。
3. AppMaster指定的Container所在Node的ContainerManagerImpl在该Node启动AppMaster, 这是在NM端。

这样， AppMaster就启动起来了。

至于谁给ApplicationMasterLauncher发送命令， 这个后面再说。


ApplicationMasterService 用于RM-AM的交互， 注册和心跳两种。 
``` 
registerApplicationMaster()
finishApplicationMaster()
allocate()
```

问题： AM的Container是怎么调度来的 ？ 要了解这个问题， 先了解RM是如何管理NM上报给集群的资源。
 






