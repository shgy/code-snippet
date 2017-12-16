在集群中， 一个Application运行完毕， 是需要清场的。这个清场，涉及到RM和NM。先看NM中重要的一环：MRAppMaster.


MRAppMaster是如何收尾的呢？ 把目光聚焦到`MRAppMaster.JobFinishEventHandler`

``` 
  private class JobFinishEventHandler implements EventHandler<JobFinishEvent> {
    @Override
    public void handle(JobFinishEvent event) {
      // Create a new thread to shutdown the AM. We should not do it in-line
      // to avoid blocking the dispatcher itself.
      new Thread() {
        
        @Override
        public void run() {
          shutDownJob();
        }
      }.start();
    }
  }
```
这个`shutDownJob()`有哪些关键步骤呢？

1. MRAppMaster.this.stop();

2. 等待5秒后， clientService.stop();


其中， `MRAppMaster.this.stop()` 会停止MRAppMaster中注册的各种服务， 包括`RMContainerAllocator`，
它的父类`RMCommunicator` 会在`serviceStop()`中调用 `unregister()`方法， 跟ResourceManager通信，通知
ResourceManager终结App, 即`ApplicationMasterProtocol.finishApplicationMaster()`。

clientService用与AppMaster跟Client通信。 `clientService.stop()` 即关闭RPC服务器。 为啥要等待5秒呢？
因为默认Client跟AppMaster通信是轮询操作， 每秒1次。 等待5秒的话， 就基本上能确保Client正确停止， 从而最大限度避免了
Client跟AppMaster通信， 但是AppMaster的服务器已经停止这一窘况了。


这里就引申出另一个问题： JobFinishEventHandler啥时会接收到JobFinishEvent事件？

1. killJob
2. Job完成， 由CommitterEventHandler来发送JobEventType.JOB_COMMIT_COMPLETED事件，从而终结AppMaster.

接下来，关注： RM接收到`ApplicationMasterProtocol.finishApplicationMaster()`的操作， 和NM的清场操作。

虽然MRAppMaster是整个Job的主管， 但是从NM的角度， 它跟普通的Container没有区别。
  

