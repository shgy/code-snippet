1. hdfs放宽了几个POSIX的限制, 来支持文件的流式操作.
2. hdfs设计的初衷是为了解决批处理问题,而非交互式问题. 因此更注重吞吐量, 而非响应时间.

3. hdfs是为了支持大文件, 而非海量的小文件. 这在应用中需要注意.

4. hdfs支持简单的一致性模型, 像Lucene一样, 一次写入, 多次读取. 适用于爬虫这类的问题.

5. NameNode执行文件系统namespace相关的操作, 例如: open/close/rename等.

   DataNode负责文件的读写任务, 同时负责block的创建/删除/建立副本等功能.

6. 每个文件在创建的时候可以指定replication个数, 并且可以修改这个值.
   DataNode定期向NameNode发送心跳和BlockReport.

7. 副本的放置对于平台的稳定性和性能影响很大.这里面有机架感知等功能.

8. 对于文件系统元数据(metadata), NameNode使用EditLog来记录.







