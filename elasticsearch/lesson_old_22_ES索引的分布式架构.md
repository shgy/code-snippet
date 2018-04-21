ES 索引的分布式架构

	ES是天生的分布式架构。分布式架构旨在解决以下的两个问题：数据量过大，单机无法存储；并发数过大，间机无法处理。

	ES的数据存储结构类比于关系性数据库的图示如下：



库(Databases) -> 表(Tables)  ->  行(Rows)       -> 列(Columns)

索引(Indices) -> 类型(Types) -> 文档(Documents) -> 字段(Fields)



在ES中，每个索引都会进行分片。主分片(primary shard)的数量在索引数据前必须指定好，添加数据后无法(暂时无法)修改。这主要是由于数据采用Hash进行路由。分片副本可以动态调整。对于给定的主分片和分片副本，最大节点数量是确定的：

Max number of nodes = Number of shards * (number of replicas + 1)

比如索引设置了number_of_shards=2;number_of_replicas=1 。则该索引用到的最大节点数：

Max number of nodes = 2*3 = 6 。

	在生产环境上，如果每台机器部署一个节点，每个节点存储一个索引，那么整个集群数据存储上限则是2台机器的容量。


