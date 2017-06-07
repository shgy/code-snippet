看了好久的Yarn, 一头雾水，直到看了《Apache Hadoop YARN》 这本书。

NodeStatusUpdater的功能：
1. 清除已经完成的container:
   addCompletedContainer() |
   isContainerRecentlyStopped()|
   removeVeryOldStoppedContainersFromCache()|
   removeOrTrackCompletedContainersFromContext() |
   getContainerStatuses() -- 如果application已经完成，则清除container

2. 通过ResourceTracer向ResourceManager发送heartbeat.

   NMNullStateStoreService  --- 这是个什么东东 ？

3. 停止NodeManager, 可重入。testStopReentrant()

4. 设置 NMContext 为 退役状态 `isDecommissioned`, 其作用待解。
   如果heartBeat接收的事件为SHUTDOWN, 则会设置。

5. 如果.registerNodeAction = NodeAction.SHUTDOWN; 则NodeManager启动不成功
   testNMShutdownForRegistrationFailure()
   NameNode在启动的时候， 会通过NodeStatusUpdater 向ResourceManager注册自己。
   如果ResourceManager返回的指令是shutdown, 则启动不成功。

6. testNMConnectionToRM()
   连接到ResourceManager会有重试机制。相关类： RetryPolicy | RetryInvocationHandler

7. 为什么NodeStatusUpdater的启动顺序放在7个service的最后一个?
   testNoRegistrationWhenNMServicesFail
   NodeStatusUpdater会向RM注册所在的NodeManager, 在此之前， 必须先确保NodeManager的所有服务启动成功。

初步总结： NodeStatusUpdater最核心的功能有3个：
1. 向RM注册NM;
2. 向RM发送心跳， 证明自己活着;
3. 清除已经停止的Container.

