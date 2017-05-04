尽管HDFS主体结构就是使用RPC支撑起来的, 但是HDFS没有使用Hadoop RPC来实现HDFS的文件读写功能,
是因为Hadoop RPC框架的效率目前还不足以支撑超大文件的读写.使用基于TCP的流式接口有利于批处理数据,
同时提高了数据的吞吐量. <Hadoop 2.x HDFS源码剖析>