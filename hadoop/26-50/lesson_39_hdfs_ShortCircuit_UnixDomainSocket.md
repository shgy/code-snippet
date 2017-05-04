
背景: 在HDFS中, 文件的读取通常需要通过DataNode. DataNode就像一个区域物流中心.
如果发个同城快递, 先要将快递送到区域物流中心,再发回来,就会很慢.


Hadoop的一大基本原则是移动计算的开销要比移动数据的开销小。因此，Hadoop通常是尽量移动计算到拥有数据的节点上。
这就使得Hadoop中读取数据的客户端DFSClient和提供数据的Datanode经常是在一个节点上，也就造成了很多“Local Reads”。

Linux UDS(UnixDomainSocket)是一种进程间的通讯方式，它使得同一个机器上的两个进程能以Socket的方式通讯. 具体的细节可以参考<Unix网路编程卷一>

使用UDS必须正常加载`native-hadoop library`

在Hadoop的实现中: 如果配置了:
```
dfs.client.domain.socket.data.traffic=true
```
但是:
```
dfs.client.read.shortcircuit=false
```
UDS依然有效, 只是统计不会统计到ShortCircuit中


```
[INFO ] 2016-12-28 18:11:27,628(39977) --> [main] org.apache.hadoop.hdfs.server.datanode.DataNode.initDataXceiver(DataNode.java:863): Listening on UNIX domain socket: /tmp/socks.1482919816543.-1518964313/testFallbackFromShortCircuitToUnixDomainTraffic.36174

INFO ] 2016-12-28 18:18:28,627(44471) --> [DataXceiver for client unix:/tmp/socks.1482920263194.498447259/testFallbackFromShortCircuitToUnixDomainTraffic.47459 [Waiting for operation #1]] org.apache.hadoop.hdfs.server.datanode.DataXceiver.requestShortCircuitShm(DataXceiver.java:423): cliID: DFSClient_NONMAPREDUCE_-899493695_1, src: 127.0.0.1, dest: 127.0.0.1, op: REQUEST_SHORT_CIRCUIT_SHM, shmId: 3163270f2f79f8e4b8d596012d1840bb, srvID: c4d5feb0-8b66-4208-9e23-784dcbb1d9d1, success: true
[INFO ] 2016-12-28 18:18:28,646(44490) --> [DataXceiver for client unix:/tmp/socks.1482920263194.498447259/testFallbackFromShortCircuitToUnixDomainTraffic.47459 [Passing file descriptors for block BP-1786454531-127.0.0.1-1482920267494:blk_1073741825_1001]] org.apache.hadoop.hdfs.server.datanode.DataXceiver.requestShortCircuitFds(DataXceiver.java:324): src: 127.0.0.1, dest: 127.0.0.1, op: REQUEST_SHORT_CIRCUIT_FDS, blockid: 1073741825, srvID: c4d5feb0-8b66-4208-9e23-784dcbb1d9d1, success: true
[INFO ] 2016-12-28 18:18:28,659(44503) --> [DataXceiver for client unix:/tmp/socks.1482920263194.498447259/testFallbackFromShortCircuitToUnixDomainTraffic.47459 [Passing file descriptors for block BP-1786454531-127.0.0.1-1482920267494:blk_1073741826_1002]] org.apache.hadoop.hdfs.server.datanode.DataXceiver.requestShortCircuitFds(DataXceiver.java:324): src: 127.0.0.1, dest: 127.0.0.1, op: REQUEST_SHORT_CIRCUIT_FDS, blockid: 1073741826, srvID: c4d5feb0-8b66-4208-9e23-784dcbb1d9d1, success: true
[INFO ] 2016-12-28 18:18:28,661(44505) --> [DataXceiver for client unix:/tmp/socks.1482920263194.498447259/testFallbackFromShortCircuitToUnixDomainTraffic.47459 [Passing file descriptors for block BP-1786454531-127.0.0.1-1482920267494:blk_1073741827_1003]] org.apache.hadoop.hdfs.server.datanode.DataXceiver.requestShortCircuitFds(DataXceiver.java:324): src: 127.0.0.1, dest: 127.0.0.1, op: REQUEST_SHORT_CIRCUIT_FDS, blockid: 1073741827, srvID: c4d5feb0-8b66-4208-9e23-784dcbb1d9d1, success: true

```

使用如下的命令可以得到UDS使用的端口.
```
$ netstat -lx | grep socks
unix  2      [ ACC ]     STREAM     LISTENING     521938   /tmp/socks.1482920263194.498447259/testFallbackFromShortCircuitToUnixDomainTraffic.47459
```

使用如下的代码可以检测shortCircuit是否启用:

```
ByteArrayOutputStream os = new ByteArrayOutputStream();
try {
  FSDataInputStream in = dfs.open(new Path(TEST_FILE));
  try {
    IOUtils.copyBytes(in, os, 1024, true);
    if (in instanceof HdfsDataInputStream) {
        HdfsDataInputStream hdfsIn = (HdfsDataInputStream) in;
        DFSInputStream.ReadStatistics readStatistics = hdfsIn.getReadStatistics();
    // os.toByteArray();
        System.out.println("Total Bytes Read Bytes: " + readStatistics.getTotalBytesRead());
        System.out.println("Short Circuit Read Bytes: " + readStatistics.getTotalShortCircuitBytesRead());
        System.out.println("Local Read Bytes:" + readStatistics.getTotalLocalBytesRead());
    }
  } finally {
    in.close();
  }
} finally {
  os.close();
}

```

关于ShortCircuitCache,

参考:
file:///opt/hadoop-2.6.0/docs/r2.6.0/hadoop-project-dist/hadoop-hdfs/ShortCircuitLocalReads.html
http://blog.csdn.net/jewes/article/details/40189263
