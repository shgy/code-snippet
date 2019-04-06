学习了`get api`和`delete api`之后，再学习相关的api, 就偏向于先从结构入手，先了解结构，再剖析具体的功能。

```
curl -XGET 'http://localhost:9200/_cluster/state?pretty'

```

`_cluster/state`用于查看es集群状态，相当于集群的roadmap。这个不常用，分析集群状态或者问题定位应该会用到。

由于这是一个只读功能接口，所以分析起来应该比较简单。
先看`TransportClusterStateAction`的层级结构:

```
TransportAction

--> HandledTransportAction

----> TransportMasterNodeAction

------> TransportMasterNodeReadAction

--------> TransportClusterStateAction
```

也就是说，`_cluster/state`只能执行在主分片节点或者本地节点。如何传递参数可以参考`RestClusterStateAction`的代码。

```
curl -XGET 'http://localhost:9200/_cluster/state?pretty&local=true'
```

看各个类的源码`TransportMasterNodeReadAction`基本上没啥逻辑，可以略过。 `TransportClusterStateAction`只有一个方法`masterOperation()`，
这就是具体的实现入口了。 那么如何定位到`master node`?  根本就不用定位，因为clusterState结构就存储了`master node`，具体的逻辑参看`AsyncSingleAction().doStart()方法`。 

```
        protected void doStart() {
            final ClusterState clusterState = observer.observedState();
            final DiscoveryNodes nodes = clusterState.nodes();
            if (nodes.localNodeMaster() || localExecute(request)) {
                  ...
            } else {
                if (nodes.masterNode() == null) {
                    logger.debug("no known master node, scheduling a retry");
                    retry(null, masterNodeChangedPredicate);
                } else {
                    taskManager.registerChildTask(task, nodes.masterNode().getId());
                    transportService.sendRequest(nodes.masterNode()...);
              }
           }
       }
```

即`ClusterState().nodes().masterNode()`。

最后汇总一下， `_cluster/state` 是只在master node 或local执行； 具体的逻辑在`masterOperation()`方法中; 核心的数据结构有4种: `nodes`, `routing_table`, `blocks`, `metadata`。 如果把ES集群比喻成一个国家，那么ClusterState就是地图，表明了各个城市的位置及路径。











