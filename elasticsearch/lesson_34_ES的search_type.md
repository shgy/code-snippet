从2.1.0以后，ES只支持两种查询类型： 

1. `query_then_fetch` 即典型的两阶段查询。类似于hadoop的map-reduce。 第一阶段从各个分片查询相关的文档；第二阶段merge重排序。

所以，使用ES做深度分页是大忌。

2. `dfs_query_then_fetch`

用于精确排序，相比`query_then_fetch`, 她有个分发阶段，获取全局的`term frequency`

scroll使用的是哪种查询类型呢？先了解scroll相关的接口。

1. 生成`Search Context`

```
curl -XGET 'localhost:9200/twitter/tweet/_search?scroll=1m' -d '
{
    "query": {
        "match" : {
            "title" : "elasticsearch"
        }
    }
}
'
``` 
2. 批量便利`Search Context`, 给`Search Context`续命

```
curl -XGET  'localhost:9200/_search/scroll'  -d'
{
    "scroll" : "1m", 
    "scroll_id" : "c2Nhbjs2OzM0NDg1ODpzRlBLc0FXNlNyNm5JWUc1" 
}
'
```


3. 查看当前的`Search Context`
```
curl -XGET localhost:9200/_nodes/stats/indices/search?pretty
```

4. 清除`Search Context`, 茴香豆的3中写法。
```
curl -XDELETE localhost:9200/_search/scroll -d '
{
    "scroll_id" : ["c2Nhbjs2OzM0NDg1ODpzRlBLc0FXNlNyNm5JWUc1", "aGVuRmV0Y2g7NTsxOnkxaDZ"]
}'

curl -XDELETE localhost:9200/_search/scroll/_all

curl -XDELETE localhost:9200/_search/scroll \
     -d 'c2Nhbjs2OzM0NDg1ODpzRlBLc0FXNlNyNm5JWUc1,aGVuRmV0Y2g7NTsxOnkxaDZ'
```  

接下来就从源码开始，看这些API在ES内部的流转操作。


索引数据
```
curl -XPUT 'localhost:9200/twitter/tweet/1?pretty' -d '
{
  "name": "John Doe"
}'
```
