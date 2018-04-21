海量数据的ES入库是比较慢的. 如何优化这一过程呢?

1 设置`refresh_interval` 为 1分钟或者更长

```

curl -XPUT localhost:9200/test/_settings -d '{

"index" : {

"refresh_interval" : "5m"

}

}'

```

2 调整ES的事务日志

```

index.translog.flush_threshold_period 默认30m

index.translog.flush_threshold_ops 默认5000条数据

index.translog.flush_threshold_size 默认200M

index.translog.disable_flush  (可以在第一次全量导数据时,关闭flush)



curl -XPUT localhost:9200/test/_settings -d '{

"index" : {

"translog.disable_flush" : true

}

}'

```
