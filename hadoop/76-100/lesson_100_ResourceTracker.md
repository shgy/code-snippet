
ResourceTRackerService其实只有两个方法：`registerNodeManager` 和 `nodeHeartbeat`
注册一个NodeManager, 接收NodeManager的心跳。

在NodeManager端， NodeStatusUpdaterImpl在启动时`serviceStart`会
调用`registerNodeManager` 将NodeManager注册到ResourceManager.
然后启动新的线程调用周期性调用`nodeHeartbeat` 发送心跳。

如果NodeManager拥有的资源不符合ResouceManager的要求，该NodeManager就会被shutdwon, 无法接入到集群中。

handleNMContainerStatus():
```
    Helper method to handle received ContainerStatus. If this corresponds to
    the completion of a master-container of a managed AM,
    we call the handler for RMAppAttemptContainerFinishedEvent.
```
