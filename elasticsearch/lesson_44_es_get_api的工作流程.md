github上es项目讲述其易用性时，用来举例的就有get api。
```
curl -XPUT 'http://localhost:9200/twitter/doc/1?pretty' -H 'Content-Type: application/json' -d '
{
    "user": "kimchy",
    "post_date": "2009-11-15T13:12:00",
    "message": "Trying out Elasticsearch, so far so good?"
}'

curl -XGET 'http://localhost:9200/twitter/doc/1?pretty=true'
```

get api 通常的用途有2点: 
1 检测添加的文档跟预期是否相符。
2 根据id获取整个文档明细， 用于搜索的fetch阶段。 


研究ES的内部机制， GET API是一个极佳的切入点。通过GET API， 可以了解到的知识点有：

a. ES的rest api实现方式。

b. ES的文档路由方式。

c. ES的RPC实现机制。

d. ES的translog.

e. ES如何使用lucene 的IndexSearcher。

f. ES如何根据id获取到lucene的`doc_id`。

g. ES如何根据lucene的`doc_id` 获取文档明细。

...


研究ES的内部机制，有助于释放ES的洪荒之力。例如：根据业务开发ES的plugin时，其内部流程是很好的借鉴。 内部细节了解越多，越不容易踩坑。


GET API的核心流程如下:

`s1: 接收客户端请求` 

```
public class RestGetAction extends BaseRestHandler {

    ...

    @Override
    public void handleRequest(final RestRequest request, final RestChannel channel, final Client client) {
       
	...

        client.get(getRequest, new RestBuilderListener<GetResponse>(channel) {
            ...
        });
    }
}

```

`s2: 在当前节点执行该请求` 

```

public class NodeClient extends AbstractClient {
    
    ...
    
    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, 
            RequestBuilder extends ActionRequestBuilder<Request, Response, RequestBuilder>> 
       void doExecute(Action<Request, Response, RequestBuilder> action, Request request, ActionListener<Response> listener) {
        TransportAction<Request, Response> transportAction = actions.get(action);
        if (transportAction == null) {
            throw new IllegalStateException("failed to find action [" + action + "] to execute");
        }
        transportAction.execute(request, listener);
    }
}

这里隐含了一个映射表, 如下:
public class ActionModule extends AbstractModule {

    ...
 
    @Override
    protected void configure() {
        ...
        registerAction(GetAction.INSTANCE, TransportGetAction.class);
        ...
    }
}

```

`s3: 定位文档所在分片` 

```
文档的定位思路很简单， 默认根据文档id, 用hash函数计算出文档的分片ShardId, 通过分片ShardId定位出NodeId。
关于文档的分片，这里后面单独一篇博客记录。

public class TransportGetAction extends TransportSingleShardAction<GetRequest, GetResponse> {

    ...
   
    @Override
    protected ShardIterator shards(ClusterState state, InternalRequest request) {
        return clusterService.operationRouting()
                .getShards(clusterService.state(), request.concreteIndex(), request.request().type(), request.request().id(), request.request().routing(), request.request().preference());
    }
}
```

`s4: 将请求转发到分片所在的节点`  

```
请求的分发，涉及到ES的RPC通信。上一步定位到NodeId, 将请求发送到该Id即可。
由于ES的每个Node代码都是一样的， 因此每个Node即承担Server也承担Client的责任，这跟其他的RPC框架有所不同。
核心方法是transportService.sendRequest() 和 messageReceived()。 

public abstract class TransportSingleShardAction<Request extends SingleShardRequest, Response extends ActionResponse> extends TransportAction<Request, Response> {

    class AsyncSingleAction {


        public void start() {
                transportService.sendRequest(clusterService.localNode(), transportShardAction, internalRequest.request(), new BaseTransportResponseHandler<Response>() {
                    ...     
                });
        }

    }


    private class ShardTransportHandler extends TransportRequestHandler<Request> {

        @Override
        public void messageReceived(final Request request, final TransportChannel channel) throws Exception {
            
            ...
            Response response = shardOperation(request, request.internalShardId);
            channel.sendResponse(response);
        }
    }

}


```

`s5: 通过id读取索引文件获取该id对应的文档信息`  

```

这里分两个阶段:
step1: 将type和id合并成一个字段，从lucene的倒排索引中定位lucene的doc_id

step2: 根据doc_id从正向信息中获取明细。


public final class ShardGetService extends AbstractIndexShardComponent {
      
      ...

    private GetResult innerGet(String type, String id, String[] gFields, boolean realtime, long version, VersionType versionType, FetchSourceContext fetchSourceContext, boolean ignoreErrorsOnGeneratedFields) {
        fetchSourceContext = normalizeFetchSourceContent(fetchSourceContext, gFields);
                ...
                get = indexShard.get(new Engine.Get(realtime, new Term(UidFieldMapper.NAME, Uid.createUidAsBytes(typeX, id)))
                        .version(version).versionType(versionType));
        
                ...
               innerGetLoadFromStoredFields(type, id, gFields, fetchSourceContext, get, docMapper, ignoreErrorsOnGeneratedFields); 
        }
    }
```

(注: 如果是realtime=true, 则先从translog中读取source, 没有读取到才从索引中读取)

s5涉及到Lucene的内部实现， 这里不展开赘述。  


最后总结一下:

Get API是ES内部打通了整个流程的功能点。从功能上看，它足够简单；从实现上看，他又串联了ES的主流程，以它为切入口，
不会像展示`You Know, for Search`的`RestMainAction`那样浮于表面；有不会向实现搜索的接口那样庞杂难懂。

