大数据方兴未艾，Hive在业界，是大数据的标配了。因此hive数据添加到ES的应用场景还是比较常见的。
学习ES官方的es-hadoop, 有从hive导数据到ES. 实验可行。
hive的版本： hive-1.1.0-cdh5.9.0

具体的步骤如下：
step1 将elasticsearch-hadoop-hive-version.jar添加到hive
```
wget https://artifacts.elastic.co/downloads/elasticsearch-hadoop/elasticsearch-hadoop-6.3.0.zip
unzip elasticsearch-hadoop-6.3.0.zip
hdfs dfs -mkdir /user/test/es_hadoop/
hdfs dfs -put elasticsearch-hadoop-hive-6.3.0.jar /user/test/es_hadoop/
ADD JAR hdfs://test/user/test/es_hadoop/elasticsearch-hadoop-hive-6.3.0.jar;
```

step2 创建Hive表：
```
CREATE EXTERNAL TABLE elastic_table(
   uuid string,
   key1 int,
   key2 int,
   day string
)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource'='index/type',
'es.nodes'='serverIP:port',
'es.index.auto.create'='TRUE',
'es.mapping.id' = 'uuid'
);
INSERT OVERWRITE TABLE elastic_table 
SELECT * FROM hive_table;

```

step3 添加数据
```
INSERT OVERWRITE TABLE elastc_table
SELECT uuid, key1,key2, day FROM source s;
```


为了避免客户端版本的问题，es-hadoop使用es的restfull接口导入数据,该接口使用的是Http协议。

通常使用ES, 首当其冲的问题就是： 如何快速将海量数据导入ES? 由于ES的数据需要建立倒排索引，所以导入数据到ES的瓶颈往往在ES这里。

本文记录了将Hive表的数据导入ES的方法。这里背后隐藏了mapreduce，即集群的威力。 这里有个系列博客，讲述如何最大限度的挖掘ES索引数据的性能，立足点是ES。 

```
https://qbox.io/blog/series/how-to-maximize-elasticsearch-indexing-performance
```
作者总结有3点：
1. 根据应用场景创建mapping, 去除不必要的字段，如`_all`, `_source`; 
这里是从应用场景下手，以避免存储不必要的信息来提升索引数据的性能。

2. 修改es/lucene默认的设置，比如
`refresh_interval`, 
`index.number_of_replicas`, 
`index.merge.scheduler.max_thread_count`,
`index.translog.interval`, 
`indices.memory.index_buffer_size`
`index.index_concurrency`
等参数。 这里是从集群的角度进行调优， 通常用于大批量导入数据到ES。

3. 如果前面两种还是没能解决问题，那就需要对集群进行横向扩展了，比如增加集群的分片数量。
   集群大了后，各个结点的功能就需要单一化，专注化了。

比如节点只承担数据相关的任务。
```
node.master: false
node.data: true
node.ingest: false
```   

bulk api的批量值需要实验，找到最佳参数。建议bulk的大小在5M～10M.

使用SSD硬盘。索引数据时，副本数设置为0。


参考:
http://note4code.com/2016/06/17/hive-%E5%90%91-elasticsearch-%E5%AF%BC%E5%87%BA%E6%95%B0%E6%8D%AE/
