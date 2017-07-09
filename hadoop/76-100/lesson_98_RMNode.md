RMNode维护了一个NodeManager的生命周期。

RMNode状态机的主线为：
NEW --> RUNNING -> UNHEALTHY -> LOST
NEW --> RUNNING -> UNHEALTHY -> DECOMMISSIONED
NEW --> RUNNING -> UNHEALTHY -> REBOOTED
NEW --> RUNNING -> REBOOTED

只有处于RUNNING状态的NodeManager才可以正常工作。

   状态                  接收事件                          发送事件
NEW --> RUNNING:  RMNodeEventType.STARTED  NodeAddedSchedulerEvent, RMAppRunningOnNodeEvent

AddNodeTransition : 上线一个Node, 分两种情况：
      一种情况是该Node原本在集群中工作， 由于某些原因下线了；
      另一种情况是该Node是新来的。


NEW-->NEW:    RMNodeEventType.RESOURCE_UPDATE

UpdateNodeResourceWhenUnusableTransition : 只是更新 节点的可用资源



RUNNING --> UNHEALTHY      RMNodeEventType.STATUS_UPDATE    NodeRemovedSchedulerEvent/NodesListManagerEventType.NODE_UNUSABLE
RUNNING --> RUNNING        RMNodeEventType.STATUS_UPDATE    NodeUpdateSchedulerEvent

StatusUpdateWhenHealthyTransition: 应该是心跳带来的

RUNNING --> DECOMMISSIONED  RMNodeEventType.DECOMMISSION    NodesListManagerEventType.NODE_UNUSABLE
DeactivateNodeTransition: 停用一个NodeManager, 这个一般是手动下线

RUNNING --> LOST           RMNodeEventType.EXPIRE            NodesListManagerEventType.NODE_UNUSABLE
DeactivateNodeTransition:  由于网络等原因， NodeMananger很久没有发来心跳消息

RUNNING --> REBOOTED      RMNodeEventType.REBOOTING        NodesListManagerEventType.NODE_UNUSABLE
DeactivateNodeTransition:  一般重启一下， 能暂时解决问题

RUNNING --> RUNNING      RMNodeEventType.CLEANUP_APP
CleanUpAppTransition:   一个任务跑完了， 清理回收资源   ? 回收完了， 记录 : 准备回收

RUNNING --> RUNNING      RMNodeEventType.CLEANUP_CONTAINER
CleanUpContainerTransition: 清理一个任务的一个Container ? 回收完了， 记录 :  准备回收

RUNNING --> RUNNING      RMNodeEventType.FINISHED_CONTAINERS_PULLED_BY_AM
AddContainersToBeRemovedFromNMTransition:  不懂

RUNNING --> RUNNING      RMNodeEventType.RECONNECTED    NodeRemovedSchedulerEvent|
                                                        NodeAddedSchedulerEvent|
                                                        RMNodeStartedEvent    |
                                                        RMAppRunningOnNodeEvent
ReconnectNodeTransition: 不懂 略过

RUNNING --> RUNNING      RMNodeEventType.RESOURCE_UPDATE  NodeResourceUpdateSchedulerEvent
UpdateNodeResourceWhenRunningTransition: 节点的可用资源有更新



看着状态眼花缭乱， 但是处理状态转换的Transition没有几个。

