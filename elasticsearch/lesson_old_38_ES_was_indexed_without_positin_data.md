使用elasticsearch搜索出现'indexed without position data'，搜索stackoverflow说原因是index中不同的type对同一个field使用了不同的类型．

如果真的是这个原因，那么可以使用如下的步骤将问题复现出来．

１创建索引的mapping

```


{

   "settings":{

        "index": {

            "number_of_shards":1,

            "number_of_replicas": 1

        }

   },

     "mappings": {

       "qyxx": {

         "date_detection": false,

         "dynamic": false,

         "properties": {

           "company_name": {

             "type": "string"

           }

         }

       },

       "xgxx": {

         "date_detection": false,

         "dynamic":false,

          "properties" : {

              "company_name": {"type" : "string", "index" : "not_analyzed"}

          }

       }

     }

}

```

```

curl -XPUT 'localhost:9200/qyxx_test' --data-binary @test.json

```

2 添加数据

```

# -*- coding: utf-8 -*-

from elasticsearch import Elasticsearch

client = Elasticsearch(hosts=[{"host":"localhost", "port":9200}])

print client.index(index='qyxx_test',doc_type='xgxx', body={"company_name": "厦门市聚达进出口有限公司"})

print client.index(index='qyxx_test',doc_type='qyxx', body={"company_name": "泉州市糖酒副食品公司"})

print client.index(index='qyxx_test',doc_type='xgxx', body={"company_name": "永康市海狮刀剪有限公司"})

```

3 检索，问题出现．

```

curl -XGET 'http://localhost:9200/qyxx_test/qyxx/_search?q=company_name:%22%E5%85%AC%E5%8F%B8%22&pretty'



{

  "error" : "SearchPhaseExecutionException[Failed to execute phase [query_fetch], all shards failed; shardFailures {[AnVlYb-tTqubMnjQ4298Dw][qyxx_test][0]: QueryPhaseExecutionException[[qyxx_test][0]: query[filtered((company_name:\"公 司\"))->cache(_type:qyxx)],from[0],size[10]: Query Failed [Failed to execute main query]]; nested: IllegalStateException[field \"company_name\" was indexed without position data; cannot run PhraseQuery (term=公)]; }]",

  "status" : 500

}

```



通过在Luke中查看索引，可以清楚的看到在ES中，同一个index的不同type是以uid来区分．

接下来就是到Lucene中验证这个问题出现的原因了．








