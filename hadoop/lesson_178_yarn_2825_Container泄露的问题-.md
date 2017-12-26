地址: https://issues.apache.org/jira/browse/YARN-2825

在NM端, Container有泄露的问题.

代码的关键点在 NodeStatusUpdaterImpl

在2.6.0版本以前, 这个通知流程是这样的:

1. NM将终结的Container通知到RM, 然后从RM的notification list中移除这些container

2. RM将终结的Container信息传递给AM, 然后AM不再维护这些信息.

这里: 如果RM在AM 移除这些终结container前挂了, 那么AM将获取不到这些container的信息了.

解决办法: 由RM通知NM去删除终结的container, 而非NM自行裁决.

这里: 需要再次回顾 NodeStatusUpdater的功能, 及 RM和 AM在这个流程中的作用.


-- ===============

再看NodeStatusUpdaterImpl, 感觉对其理解又深了点.
```
// startStatusUpdater()

// Explicitly put this method after checking the resync response. We
// don't want to remove the completed containers before resync
// because these completed containers will be reported back to RM
// when NM re-registers with RM.
// Only remove the cleanedup containers that are acked
removeOrTrackCompletedContainersFromContext(response
      .getContainersToBeRemovedFromNM());
```

NM向RM汇报其节点上Container的状态:  NodeStatusUpdaterImpl ---> ResourceTrackerService
RM返回需要移除的Container信息;
NM移除处于DONE状态的Container, 如此反复.
NM是一个典型的尽职但不越权的下属. 对于NodeStatusUpdaterImpl, RM端的对接人是ResourceTrackerService.

这里延伸一点: 说说RM返回给NM的两个指令: `RESYNC` 和 `SHUTDOWN`

1. `RESYNC` 重新同步
  如果ResourceTrackerService在rmContenxt中没有找到该NM, 则会要求NM重新向RM注册.
  NM的注册就是 停止NodeStatusUpdater线程 --> 注册NM  --> 启动NodeStatusUpdater线程

  如果NM给RM回复的信息过时了, 则会要求NM重新向RM注册. 并且重启RMNodeImpl. 重启RMNodeImpl的过程就不展开了.

2. `SHUTDOWN` 关闭NM
  如果NM在RM端不是合法的(已经有案底), 则会驱逐出境. 这个是正确下线NM的关键. 将要下线的NM的host/ip信息记录到
  nodes.include-path文件中, 然后使用`refreshNodes` 更新集群的配置即可.

  NM收到`SHUTDOWN`如何处理? 退出NodeStatusUpdater的循环, 给NodeManager发送`shutdown`的事件,停止NM.

疑问: NM上正在运行的Container咋办呢? NM都下线了, 哪里顾得上这么多 ... 


-- ===============================================================================

1. NM向RM上报 Container的节点状态: NodeStatusUpdaterImpl ----> ResourceTrackerService

2. ResourceTrackerService 通知 RMNodeImpl 同步信息: 
```
// 4. Send status to RMNode, saving the latest response.
this.rmContext.getDispatcher().getEventHandler().handle(
    new RMNodeStatusEvent(nodeId, remoteNodeStatus.getNodeHealthStatus(),
        remoteNodeStatus.getContainersStatuses(), 
        remoteNodeStatus.getKeepAliveApplications(), nodeHeartBeatResponse));
```
  2.1 正在运行的container会添加到 `rmNode.launchedContainers`容器中.
  2.2 将newlyLaunchedContainers 和 completedContainers 用`rmNode.nodeUpdateQueue`管理起来


3. RMNodeImpl 通知 调度器 FifoScheduler(发送事件`SchedulerEventType.NODE_UPDATE`).
  FifoScheduler.nodeUpdate处理该事件. 从`rmNode.nodeUpdateQueue`取出container信息.
  对于新启动的Container, 通过SchedulerApplicationAttempt通知RMContainerImpl, 该container已经启动,不能回收
  (RM会对已经分配但是一段时间后没有启用的container进行回收)
  对于已经完成的container, 通知`FiCaSchedulerApp`对象 和 `FiCaSchedulerNode`对象. 这些都是调度器内部的元素.

3.1 `FicaSchedulerApp.containerCompleted()`
  从`livecontainers`, `newlyAllocatedContainers`, `containersToPreempt`容器中删除该container
  通知RMContainerImpl状态机;  RMContainerImpl接收到该事件, 
  通知RMAppAttemplImpl状态机; 该状态机会将container信息存储到`appAttempt.justFinishedContainers`对象中.
  
3.2 `FiCaSchedulerNode.releaseContainer()`
  从 `launchedContainers`容器中删除container. 回收container的资源: `availableResource`, `usedResource`


到这里 `SchedulerEventType.NODE_UPDATE` 这条线先暂停.

4. 另一条线就是AppMaster会周期性地向RM申请资源, 业务对接是由`ApplicationMasterService`负责. 每次`allocate()`方法被调用,
   都会执行`appAttempt.pullJustFinishedContainers()`, 该方法我们关注两点: 
   1. 从`justFinishedContainers`取出finishedContainers, 进而将finishedContainer维护到`finishedContainersSentToAM`容器中.
   
   2. 通知RMNodeImpl, 将`finishedContainersSentToAM`容器维护的container存贮到`rmNode.containersToBeRemovedFromNM`容器中.
   
   (需要注意这两段代码的执行顺序: 2先1后). 
   
   
   
5. 回到`SchedulerEventType.NODE_UPDATE` 这条线, ResourceTrackerService 在回复NM时, 会从RMNodeImpl中取出`containersToBeRemovedFromNM`发给NM.
   NM接到RM的回复后,才正式从NM的context中移除Container. 

以上就是一个Container使命完结后整个葬礼的完整流程. 
  
