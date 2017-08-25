ApplicationMaster与ResourceManager的沟通方式， 也只有3个方法。
```
registerApplicationMaster()
allocate()
finishApplicationMaster()
```

在ResourceManager端的实现类为`ApplicationMasterService`

1. registerApplicationMaster

   调用amLivelinessMonitor.receivedPing()方法记录心跳
   发送RMAppAttemptRegistrationEvent事件通知相关的处理组件

2. allocate()

   调用amLivelinessMonitor.receivedPing方法记录心跳
   发送RMAppAttemptStatusupdateEvent事件通知相关的处理组件
   调用相关的调度器分配资源

   注： 这个方法是Yarn调度的关键入口， 先不着急弄清楚， 因为着急也弄不清楚。

3. finishApplicationMaster

    调用amLivelinessMonitor.receivedPing()方法记录心跳
    发送RMAppAttemptUnregistrationEvent事件通知相关的处理组件