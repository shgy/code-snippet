ES作为一个NoSQL数据库， CRUD是其核心的功能。前面学习了`get api`的主要流程，这里探索一下`delete api`的实现原理。 优先选择`delete api`而非`index api`, 主要是觉得删除貌似更容易， 选择`delete api`学习曲线应该比较平缓。


ES相关功能Action的命名很统一， 比如`get api`, 对应实现类的名称为`TransportGetAction`, `delete api`对应实现类的名称也是依样画葫芦: `TransportDeleteAction`。 

但是学习TransportDeleteAction, 其核心流程在其父类: `TransportReplicationAction`。 个人觉得这个名字起得不好，让人以为是只会在副本上执行相关功能的意思。

了解`TransportReplicationAction`之前，先说一下`delete api`的执行流程，就是剧透结果，再解析剧情。

```
从ES中删除一个文档的过程如下: 先从主分片中执行删除操作，再在所有的副本分片上执行删除操作。 所以核心流程分三步:

s1: 从请求节点路由到主分片节点。

s2: 在主分片节点执行删除操作。

s3: 在所有的副本分片上执行删除操作。
```

按如上所述, 在`TransportReplicationAction`类中，对应着三个子类:

|class name      |  功能                             |
|----------------|:----------------------------------|
|ReroutePhase    | 将请求路由到primary 分片所在的节点|
|PrimaryPhase    | 在主分片执行任务                  |
|ReplicationPhase| 在所有的replica分片上执行任务     | 

这个结构是通用的，就像模板一样。 这个类有个注释，解释了类的运行流程:
```
/**
 * Base class for requests that should be executed on a primary copy followed by replica copies.
 * Subclasses can resolve the target shard and provide implementation for primary and replica operations.
 *
 * The action samples cluster state on the receiving node to reroute to node with primary copy and on the
 * primary node to validate request before primary operation followed by sampling state again for resolving
 * nodes with replica copies to perform replication.
 */

```
解释起来有如下的关键几点:
```
1. 基于该类的请求先会在primary shard执行，然后在replica shard执行。
2. 具体执行的操作由子类实现，比如`TransportDeleteAction`就实现了删除的操作。
3. 每个节点在执行相关的操作前需要基于cluster state对请求参数进行验证。这个验证对应的方法就是`resolveRequest`
```

基于这个流程，可以看出，删除操作还是比较重量级的， 副本越多，删除的代价就越大。 

由于ES每个节点代码都是一样的，所以默认情况下每个节点的可扮演的角色是自由切换的。 这里主要是在研读`transportService.sendRequest()`方法时的一个小窍门。 比如代码:
```
        void performOnReplica(final ShardRouting shard) {
            // if we don't have that node, it means that it might have failed and will be created again, in
            // this case, we don't have to do the operation, and just let it failover
            final String nodeId = shard.currentNodeId();

            ...

            final DiscoveryNode node = nodes.get(nodeId);
            transportService.sendRequest(node, transportReplicaAction, ... ){
                ...
            }
        }

```
这里`transportService.sendRequest()`后，接受的逻辑在哪里呢？
```
  transportService.registerRequestHandler(actionName, request, ThreadPool.Names.SAME, new OperationTransportHandler());
  transportService.registerRequestHandler(transportPrimaryAction, request, executor, new PrimaryOperationTransportHandler());
 // we must never reject on because of thread pool capacity on replicas
  transportService.registerRequestHandler(transportReplicaAction, replicaRequest, executor, true, true, new ReplicaOperationTransportHandler());
```
也就是说`transportService.sendRequest()`的第二个参数`action`和`transportService.registerRequestHandler()`的第一个参数`action`是一一对应的。
遇到`transportService.sendRequest()`直接通过`action`参数找到对应的Handler即可。比如`PrimaryOperationTransportHandler`就是用于接收`ReroutePhase().run()`方法中发送出去的请求。

回到`TransportDeleteAction`, 来理解ES删除的逻辑，整个类就只需要理解2个方法:
```
# 在primary shard执行的删除逻辑
shardOperationOnPrimary()

# 在replica shard执行的删除逻辑
executeDeleteRequestOnReplica()
```

这里面就是删除的具体逻辑，属于Lucene层的代码，跟ES的关联就不大了。这里就不展开。


关于删除，ES提供的是`delete by id`的思路。 早期ES是支持通过query批量删除的，后来觉得这个功能太过危险，就将`delete by query`做成了Plugin, 由用户自行安装插件后才能使用。 关于ES批量删除的思路，可以参考`delete by query`插件的源码，大体思路是通过`scroll query` 按条件查询出doc id, 然后使用`client.bulk()`进行删除。

最后，由于`TransportReplicationAction`是个比较通用的模式，所以ES其他的功能也是基于这个模式的， 比如: `TransportIndexAction`。





























