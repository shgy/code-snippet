学习`testMemlockLimit`, 搜索发现这样一个主题:`HDFS缓存管理操作实战`
HDFS提供了一个高效的缓存加速机制——Centralized Cache Management，可以将一些经常被读取的文件（例如Hive中的fact表）pin到内存中。
这些DataNode的缓存也是由NameNode所管理的（NameNode所管理的cache依然是以block形式，而DataNode也会定期向NameNode汇报缓存状态），
而客户端可以高效得读取被缓存的数据块；为了能锁定内存，该实现依赖于JNI使用libhadoop.so，
所以POSIX资源限制也要进行相应的设置（ulimit -l），并确保下面的参数被设置。

`dfs.datanode.max.locked.memory`

该参数用于确定每个DataNode给缓存使用的最大内存量。设置这个参数和ulimit -l时，需要注意内存空间还需要一些内存用于做其他事情，
比如，DataNode和应用程序JVM堆内存、以及操作系统的页缓存，以及计算框架的任务。所以不要使用太高的内存百分比。



参考:
http://ju.outofmemory.cn/entry/106653
http://www.infoq.com/cn/articles/hdfs-centralized-cache
file:///opt/hadoop-2.6.0/docs/r2.6.0/hadoop-project-dist/hadoop-hdfs/CentralizedCacheManagement.html