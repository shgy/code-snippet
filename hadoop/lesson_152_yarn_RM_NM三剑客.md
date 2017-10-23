RM-NM交互的3个组件： 
```
NMLivelinessMonitor
NodesListManager
ResourceTrackerService
```

先看ResourceTrackerService， NM启动后会调用ResourceTrackerService向RM注册自己。

ResourceTrackerService.registerNodeManager() 这个方法，添加机器， 相当于集群的扩容。
方法执行的主要步骤：

1. 检测NM的版本， 如果版本不满足需求， 关闭NM.
2. 检测NM的host是否是有效节点， 如果无效， 关闭NM。
3. 检测NM上报的资源是否符合集群的标准， 如果不符合， 关闭NM。
4. 创建RMNodeImpl对象， 该对象是NM在RM端的马甲。 这个RM需要管理的第一个状态机。
5. 发送RMNodeEventType.STARTED事件给RMNodeImpl， 宣告NM启动成功。
6. 将NodeId注册到 NMLivelinessMonitor, 这样，就不会出现没有人管NM死活的问题了。


ResourceTrackerService.nodeHeartbeat() 这个方法用于NM向RM发送心跳， 告诉RM自己还活着， 是有价值的。
nodeHeartbeat机制， 可以使NM平稳下线。 相关的内容参考NodesListManager.refreshNodes()方法。

方法执行的主要步骤：
1. 发送心跳给nmLivelinessMonitor， 这个是NM存活的关键。
2. 检测NM是否是有效节点， 如果无效， 关闭NM。发送RMNodeEventType.DECOMMISSION事件给RMNodeImpl。
3. 检测当前心跳是否是NM跟最近一次RM的交互，如果id差为1，则表明是重复请求，否则是过期的请求。
   如果请求过期， 则发送RMNodeEventType.REBOOTING事件个RMNodeImpl. *这个场景没有弄明白*。
4. 发送RMNodeEventType.STATUS_UPDATE事件给RMNodeImpl。

总结： 
 ResourceTrackerService 负责两个动作: `注册`跟`心跳`，基本上就是在维护RMNodeImpl状态机的状态。


NodesListManager和NMLivelinessMonitor都是 ResourceTrackerService 的成员变量。

refreshNodes() 比如更新`RM_NODES_EXCLUDE_FILE_PATH`后， 使用refreshNodes(), 就可以实现平稳的NM下线。
所以 NodesListManager 的功能很简单： 维护正常节点和异常节点列表。

发送RMAppEventType.NODE_UPDATE事件给该节点上运行的每个app, 即RMAppImpl对象，这是RM维护的第二个状态机。*貌似这个事件没有啥作用*

NMLivelinessMonitor 和 AMLivelinessMonitor是同一类东西。具体参看lesson_95.
