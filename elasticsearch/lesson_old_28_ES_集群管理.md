ES集群管理

ES集群有3种健康状态，分别用红、黄、绿3种颜色表示。这三种颜色也正好是交通红绿灯的三种颜色，其意义也就很明显了。

status字段提供一个整体的标识来指示集群的功能是否可用。三种颜色表示：

颜色|意义

--|--

green|所有主要和复制的分片都可用

yellow|所有主分片可用，但不是所有复制分片都可用

red|不是所有的主分片都可用



在ES中，用如下的命令查看集群的健康状态：

curl localhost:9200/_cluster/health?pretty



在我的电脑上，开启了两个节点的伪集群，其返回值如下：

```

{

  "cluster_name" : "elasticsearch",

  "status" : "green",

  "timed_out" : false,

  "number_of_nodes" : 2,

  "number_of_data_nodes" : 2,

  "active_primary_shards" : 5,

  "active_shards" : 10,

  "relocating_shards" : 0,

  "initializing_shards" : 0,

  "unassigned_shards" : 0

}

```

暂时忽略其它的参数，我们只关注status参数，其值为green。

用源码跟踪一下其运行过程：

```

HttpRequestHandler.messageReceived()

--->NettyHttpServerTransport.dispatchRequest()

--->HttpServer.dispatchRequest()

--->HttpServer.internalDispatchRequest()

--->RestController.dispatchRequest()

--->RestClusterHealthAction.handleRequest()

--->AbstractClusterAdminClient.health()

--->TransportClusterHealthAction.masterOperation()

--->TransportClusterHealthAction.clusterHealth()

```


