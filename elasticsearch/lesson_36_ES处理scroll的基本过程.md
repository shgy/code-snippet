scroll的基本原理是为查询创建一个`search_context`, 这相当于一个快照。索引中文档的删除，优化都被隔离开来了。 因此，一个近实时引擎
是不适合使用大量的scroll, 它会占用太多的文件描述符。scroll开发出来，本意是为了处理ES版本的升级，不同ES索引或者集群同步数据用的。
但是对于每天同步一次这种业务场景， scroll是可以用于业务的。
 

```
curl -XGET localhost:9200/_nodes/stats/indices/search?pretty
```

1. RestController.executeHandler()  

得到： RestNodesStatsAction. 看RestNodesStatsAction的url配置
```
        controller.registerHandler(GET, "/_nodes/stats", this);
        controller.registerHandler(GET, "/_nodes/{nodeId}/stats", this);
        controller.registerHandler(GET, "/_nodes/stats/{metric}", this);
        controller.registerHandler(GET, "/_nodes/{nodeId}/stats/{metric}", this);
        controller.registerHandler(GET, "/_nodes/stats/{metric}/{indexMetric}", this);
        controller.registerHandler(GET, "/_nodes/{nodeId}/stats/{metric}/{indexMetric}", this);
```

请求符合第5个规则`/_nodes/stats/{metric}/{indexMetric}`


2. RestNodesStatsAction.handleRequest()





3. client的请求
```
client.admin().cluster().nodesStats(nodesStatsRequest, new RestToXContentListener<NodesStatsResponse>(channel));
```

4. Node接受请求，返回结果
```
org.elasticsearch.action.support.nodes.TransportNodesAction$NodeTransportHandler

TransportNodesStatsAction.NodeStatsAction

org.elasticsearch.indices.IndicesService.stats

```

=================================================================

问题: 

scroll only doc为啥会这么快？





参考：
(感觉这个地方的es资源挺不错的)
https://thoughts.t37.net/tagged/elasticsearch
