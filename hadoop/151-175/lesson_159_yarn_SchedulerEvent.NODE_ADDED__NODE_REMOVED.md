NODE_ADDED: 

事件的来源是 RMNodeImpl.AddNodeTransition()

事件的处理很简单： 
1. 将Node添加到nodes变量中。
2. 增加Scheduler管理的资源总量。
3. 恢复节点上正在运行的Container, 至于恢复的细节， 先略过。

NODE_REMOVED:

1. 清除Node上运行的Container, 清除的细节， 先略过.
2. 从nodes变量中删除节点。
3. 减少资源的总量。

CONTAINER_EXPIRED:

1. 相关的Node, app 清理container. 
2. 从usedResource中除去Container包含的资源。

注意由于Container可能实际运行在其他的机器上， 所以， 这个清理是异步的，
由node的心跳带回目标节点，完成实际的删除操作。



