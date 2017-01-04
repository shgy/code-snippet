在集群应用中, 有一个典型的应用场景: 冷热数据. 比如, 大部分公司的集群都用来分析业务日志数据.
而随着时间的推移, 年代久远的日志数据用到的频率已经不高了. 在集群中, 如果将它们跟近一个月的日志数据同等对待, 显然浪费集群的资源.
hadoop提出的解决方案就是: 冷热数据 异构存储.

对于异构存储, HDFS定义了如下的四种存储类型
 ```
  RAM_DISK(true);  # 内存
  SSD(false),      # 固盘
  DISK(false),     # 硬盘
  ARCHIVE(false),  # 归档
```

参考:
http://blog.csdn.net/androidlushangderen/article/details/51105876