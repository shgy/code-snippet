NodeResourceUpdateSchedulerEvent事件在RMNodeImpl状态机中生成。
在
```
.addTransition(NodeState.RUNNING, NodeState.RUNNING,
 RMNodeEventType.RECONNECTED, new ReconnectNodeTransition())
.addTransition(NodeState.RUNNING, NodeState.RUNNING,
 RMNodeEventType.RESOURCE_UPDATE, new UpdateNodeResourceWhenRunningTransition())
```
下面两个状态转移中用到。

这个事件的处理很简单： 更新YarnScheduler对应节点的资源。