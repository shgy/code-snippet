我们知道， 对于NM来说， 可能它只承载了整个App需要Container的一部分， 但是NM也维护了一个ApplicationImpl状态机。其目的何在呢？
每个Container运行完毕后， 一般是不能马上清理的， 因为Container输出的结果可能还有其他地方需要用到。
但是App执行完毕后， 其运行过程中产生的数据又是不能不清理的。 不然用不了多久， 整个NM就会因为硬盘或者内存不足而没法工作了。

首先， 看看ApplicationImpl状态机的主线。
New --> INITING  --> RUNNING --> FINISHING_CONTAINERS_WAIT  --> APPLICATION_RESOURCES_CLEANINGUP --> FINISHED.

创建 --> 初始化 --> 运行  --> 等待Container结束 --> 清理资源 --> 结束。

整条线貌似只有`FINISHING_CONTAINERS_WAIT`不好理解： 如果该App运行的所有container都完成了， 那么进入 `APPLICATION_RESOURCES_CLEANINGUP`
否则， 进入`FINISHING_CONTAINERS_WAIT`。

看ApplicationImpl状态机， 只有接收到一种事件， App才会进入到`FINISHING_CONTAINERS_WAIT`状态。 那就是`FINISH_APPLICATION`.

我们知道NM的入口只有rpc (appMaster)和 heartbeat (RM). 

ContainerManagerImpl.handle()方法接收到`FINISH_APPS`时会生成`ApplicationFinishEvent`事件。而`FINISH_APPS`只会在两种情形下产生：
```
1. NM关闭 (ContainerManagerImpl.cleanUpApplicationsOnNMShutDown() )。 
2. NodeStatusUpdaterImpl接收到了来自RM的`appsToCleanup`信息。
```

也就是说，正常情况下， 整个App的生命流程应该是这样的。

用户提交App -->   RM启动AppMaster --> 
AppMaster向RM注册 -->  AppMaster启动各个Container --->
各个NM上的Container向RM汇报运行情况  --> AppMaster通过周期性的allocate方法获取container的运行状态 --> 
AppMaster等待各个启动的container完成 -->
AppMaster向RM注销 --> RM向Client反馈App已经完成  --> 结束任务,程序退出。

 
