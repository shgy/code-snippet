关系型数据库最强悍的地方就是关系。向ES这种NoSQL， 最弱的就是关系。

但是通常业务场景下，我们需要保留关系。
 
比如我们希望取评论含特定关键词的商品。比如评论中同时有“超值”和“贵”的商品，或者包含其他关键词等信息。

这时候，如果没有关系，我们需要两步：取符合条件的评论， 通过评论中的商品ID取商品。这基本上是个
```
select * from product where id in (select product_id from comment where keywords like "%XXX%" group by product_id)
```
操作。 sql有like+group 基本上是个很耗时的操作了。

es可以建parent-child关系，按文档过一遍吧。

step1: create mapping

```

curl -XPUT 'http://localhost:9200/parentchild' -d '{
    "settings": {
        "index": {
            "number_of_shards": 1,
            "number_of_replicas": 1
        }
    },
    "mappings": {
       "branch": {},
       "employee": {
         "_parent": {
            "type": "branch" 
         }
      }    
    }
}'
```
step2: 添加数据





参考：
https://www.elastic.co/guide/en/elasticsearch/guide/current/relations.html
