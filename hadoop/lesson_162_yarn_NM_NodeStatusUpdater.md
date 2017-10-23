
一个NM启动后， 需要主动向RM登记， 前来面试`registerWithRM()`， 面试通过就开始每天上班打卡`resourceTracker.nodeHeartbeat()`。

`registerWithRM` 会向RM上报这5种信息： nodeId, httpPort, totalResource, nodeManagerVersionId, containerReports, runningApplications。

RM返回的是NodeAction和MasterKey.


`resourceTracker.nodeHeartbeat` 上报的是NodeStatus数据， 这里面也包含了containersStatuses和runningApplications。
RM返回的是NodeAction， ContainersToBeRemovedFromNM， ContainersToCleanup，ApplicationsToCleanup

 ContainersToBeRemovedFromNM 只是将ContainerId从NM的context中移除。
 
ContainersToCleanup 会 发送 ContainerManagerEventType.FINISH_CONTAINERS 事件给 ContainerManagerImpl 事件处理器。
ApplicationsToCleanup 会 发送 ContainerManagerEventType.FINISH_APPS 事件给 ContainerManagerImpl 事件处理器。


既然已经了解了RM， 那么这里可以从RM的角度看看是如何组织数据返回给NM. RM端的对接人是ResourceTrackerService

我们知道， 在RM端， RMNodeImpl可视为NM的一个代理。在RMNodeImpl对象中， 就有如下的3个结构：
``` 
  /* set of containers that need to be cleaned */
  private final Set<ContainerId> containersToClean = new TreeSet<ContainerId>(
      new ContainerIdComparator());

  /*
   * set of containers to notify NM to remove them from its context. Currently,
   * this includes containers that were notified to AM about their completion
   */
  private final Set<ContainerId> containersToBeRemovedFromNM =
      new HashSet<ContainerId>();

  /* the list of applications that have finished and need to be purged */
  private final List<ApplicationId> finishedApplications = new ArrayList<ApplicationId>();
```
正好对应着 `resourceTracker.nodeHeartbeat`的应答消息。








