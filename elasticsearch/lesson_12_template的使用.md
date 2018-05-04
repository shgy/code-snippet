es提供template功能的出发点在哪里呢? 作为NoSQL数据库, ES在数据入库前是不做schema设定的, 也就是不限定数据字段.
这对日志类型的数据来说, 是个利好的场景.  但是这种不设定schema的做法, 有时有太过自由. 有些业务场景, 我们需要预先
设定field的分词方式. 这时固然可以使用mappings解决. 但是业务接入前要通知一下,先建个索引, 想想有点不智能. 
有没有更灵活一点的做法呢? templates

templates的使用很简单, 但是想用好, 不出问题或者少出问题, 得有一整套流程:

1. 创建template
```
curl -XPUT localhost:9200/_template/template_1 -d '
{
    "template" : "te*",
    "settings" : {
        "number_of_shards" : 1,
        "number_of_replications":2
    },
    "mappings" : {
        "type1" : {
            "_source" : { "enabled" : false }
        }
    }
}
'
```

2. 查看template
```
curl -XGET localhost:9200/_template/template_1?pretty
```

3. 如果templates创建出错, 删除template
```
curl -XDELETE localhost:9200/_template/template_1
```

4. template建好后, 要测试一下是否符合预期, 添加一条数据
```
$ curl -XPUT 'http://localhost:9200/template_test/tweet/1' -d '{
    "user" : "kimchy",
    "post_date" : "2009-11-15T14:12:12",
    "message" : "trying out Elasticsearch"
}'
```
5. 查看集群的状态, 如果分片副本设置错误, 有可能集群变成yellow
```
curl -XGET 'http://localhost:9200/_cluster/health?pretty=true'
```

6. 查看索引结构及数据样例
```
curl -XGET 'http://localhost:9200/twitter/_settings,_mappings?pretty'
curl -XGET 'http://localhost:9200/template_test/tweet/1'
```

经过后面这些验证, 一般就能规避大多数问题了.


