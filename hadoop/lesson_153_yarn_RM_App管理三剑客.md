
Application管理模块一共有3个类：
```
ApplicationACLsManager
RMAppManager
ContainerAllocationExpirer
```


先学 RMAppManager

RMAppManager.submitApplication() 
1. 创建RMAppImpl。 这个是RM端的第二个状态机。
2. 发送RMAppEventType.START事件给RMAppImpl对象。

RMAppManager.finishApplication()
1. 将AppId添加到completedApps队列中。 (为了防止completedApps队列爆仓， checkAppNumCompletedLimit()会清理)
2. 写审计日志。



RMAppManager负责的任务很简单， 所以 《Hadoop技术内幕》只有一句话解说其功能： 管理应用程序的启动和关闭。

ApplicationACLsManager只有一个核心方法： `checkAccess`。 
《Hadoop技术内幕》对其功能的解说也很简略： 管理应用程序访问权限。

ContainerAllocationExpirer 本质上跟 NMLivelinessMonitor和AMLivelinessMonitor是一样的。
ContainerAllocationExpirer 监控的对象是 Container
NMLivelinessMonitor        监控的对象是 NodeManager
AMLivelinessMonitor        监控的对象是 ApplicationMaster

如果Container分配一段时间后， 没有前来注册， 则认为已经过期， 需要回收。
过期会发送SchedulerEventType.CONTAINER_EXPIRED事件给CapacityScheduler对象。



