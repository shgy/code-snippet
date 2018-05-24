业务场景需要快速从es中导出数据。
es-hadoop的方式是： 索引有几个分片，创建几个map任务。然后每个map任务处理一个分片。
这样的话，es数据写入到hadoop， 是由es索引的分片数确定的。
由于业务本身的数据可以再拆分， 比如假如数据中有省份字段，那么每个省份一个query, 就能拆出34个query。
假如es有5个分片，那么就可以拆分出34×5=170个map任务了。
这样以来，整个程序运行的时间就不再受限于es scroll了。

索引数据有104条， 指定分片后，只查对应的分片22条， 是想要的结果。
```
$ curl -XGET http://localhost:9200/twitter/_search?preference=_shards:0
```


