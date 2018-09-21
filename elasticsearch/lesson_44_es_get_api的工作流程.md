github上es项目讲述其易用性时，用来举例的就有get api。
```
curl -XGET 'http://localhost:9200/twitter/doc/1?pretty=true'
curl -XGET 'http://localhost:9200/twitter/doc/2?pretty=true'
curl -XGET 'http://localhost:9200/twitter/doc/3?pretty=true'
```

get api 通常的用途是检测添加的文档跟预期是否相符。另一个用途就是根据id直接获取文档内容: 当ES用于检索博客，代码这类文本时。通常分两步走： 第一步根据搜索词获取文档id及打分，第二步是根据文档id获取详情。通常第一步不获取明细， 第二步是按需加载。双管齐下，用于提升系统的吞吐量。

研究ES的内部机制， GET API是一个极佳的切入点。通过GET API， 可以了解到的知识点有：
a. ES的rest api实现方式。
b. ES的文档路由方式。
c. ES的RPC实现机制。
d. ES的translog.
e. ES跟lucene对接。
f. ES如何根据id获取到lucene的`doc_id`。
g. ES如何根据lucene的`doc_id` 获取文档明细。
...


学习GET API的实现机制， 有助于理解ES的内部机制，释放ES的洪荒之力。 自己根据业务开发ES的plugin时，其内部流程也是很好的借鉴。

GET API的核心流程如下:

接收客户端请求 -->  在当前节点执行该请求  -->   定位文档所在分片  ---->  将请求转发到分片所在的节点  -->   通过lucene到拍索引获取lucene `doc_id`  -->  通过lucene `doc_id` 读取fdx/fdt, 获取文档明细   --> 返回结果










s1: 通过id定位到分片
s2: 通过type + id, 定位到doc_id和reader
s3: 通过doc_id和reader, 定位到doc的明细
