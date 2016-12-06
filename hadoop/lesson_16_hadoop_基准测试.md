一个集群部署安装好后, 一般需要跑一些任务来测试集群功能是否正常, 比如网络带宽, 磁盘IO.
或者对集群进行参数调优. 这些功能Hadoop都有提供. 这些功能一般都在`hadoop-*test*.jar` 和 `hadoop-*examples*.jar` 包中.

HDFS吞吐量的基准测试
```
hadoop jar /opt/hadoop-2.6.0/share/hadoop/hdfs/hadoop-hdfs-2.6.0-tests.jar org.apache.hadoop.hdfs.BenchmarkThroughput
```


1 RPC连接测试: MiniRPCBenchmark measures time to establish an RPC connection to a secure RPC server.
```
 hadoop jar share/hadoop/common/hadoop-common-2.6.0-tests.jar org.apache.hadoop.ipc.MiniRPCBenchmark 100
```
2 RPC调用测试:  enchmark for protobuf RPC.
```
hadoop jar share/hadoop/common/hadoop-common-2.6.0-tests.jar org.apache.hadoop.ipc.RPCCallBenchmark -c 10 -s 1 -t 120
```

参考:
http://www.michael-noll.com/blog/2011/04/09/benchmarking-and-stress-testing-an-hadoop-cluster-with-terasort-testdfsio-nnbench-mrbench/