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


参考：
(感觉这个地方的es资源挺不错的)
https://thoughts.t37.net/tagged/elasticsearch
