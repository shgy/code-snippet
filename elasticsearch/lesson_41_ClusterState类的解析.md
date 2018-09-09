1. 只有master node可以更新ClusterState

2. InternalClusterService 单线程更新ClusterState结构


ClusterState的作用，这里需要梳理清楚.

1. cluster state 对象除 RoutingNodes外不可变。这一点对程序间线程的交互特别重要。

2. RoutingNodes基于RoutingTable的需求创建。

3. RoutingTable在publishing和applying 阶段更新，(这个可以通过代码验证)

4. cluster state只能通过master node更新。

5. 所有的更新操作只有一个线程入口： InternalClusterService

6. 每次更新后， DiscoveryService.publish() 方法将新版的集群状态更新到集群的各个节点。

7. 实际的操作由DiscoverService授权给相关的 discovery type. 比如： LocalDiscovery和Zen Discovery.

8. cluster state实现了 Diffable接口，允许publish 变更的部分，而非整个状态数据。

9. 如果节点没有历史的版本，则从master同步整个状态数据。

10. 为了确保版本的正确性， stateUUID机制是必须的。

问题：
1. 同步是推还是拉？
 
