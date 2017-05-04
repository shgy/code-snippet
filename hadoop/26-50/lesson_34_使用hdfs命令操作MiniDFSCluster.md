突然想验证一把: hdfs的命令行是否可以操作MiniDFSCluster, 今天实验了一把, 竟然可以. 把过程记录一遍.
使用MiniDFSCluster实验,将NameNode的绑定到9000端口.
```
MiniDFSCluster cluster = new MiniDFSCluster.Builder(new HdfsConfiguration())
						.nameNodePort(9000).numDataNodes(1).build();
				cluster.waitActive();
```
然后修改hadoop-2.6.0/etc/hadoop/core-site.xml
```
 <property>
   <name>hadoop.tmp.dir</name>
   <value>file:/opt/hadoop-2.6.0/hadoop-data/tmp</value>
 </property>
 <property>
   <name>fs.defaultFS</name>
   <value>hdfs://localhost:9000</value>
 </property>
</configuration>

```
然后就可以直接使用hdfs命令了:
```
shgy@shgy-thinkpad:/opt/hadoop-2.6.0$ hdfs dfs -ls /
shgy@shgy-thinkpad:/opt/hadoop-2.6.0$ hdfs dfs -mkdir /user
shgy@shgy-thinkpad:/opt/hadoop-2.6.0$ hdfs dfs -ls /
```


TraceAdmin

file:///opt/hadoop-2.6.0/docs/r2.6.0/hadoop-project-dist/hadoop-common/Tracing.html
```
hadoop jar /opt/hadoop-2.6.0/share/hadoop/hdfs/hadoop-hdfs-2.6.0-tests.jar \
org.apache.hadoop.tracing.TraceAdmin -help

hadoop jar /opt/hadoop-2.6.0/share/hadoop/hdfs/hadoop-hdfs-2.6.0-tests.jar \
org.apache.hadoop.tracing.TraceAdmin -host localhost:9000 -add \
-class org.htrace.impl.LocalFileSpanReceiver \
-Clocal-file-span-receiver.path=/home/shgy/tmp/trace.file
```
关于traceadmin, 类似与linux的strace命令, 只是它是分布式的.
Hadoop 2.6的新特性包含了Trace功能，一个类似于Google Dapper的分布式跟踪工具，为Hadoop系列应用提供请求跟踪和性能分析

代码:
```
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.tracing.SpanReceiverHost;
import org.apache.hadoop.util.ToolRunner;
import org.htrace.Sampler;
import org.htrace.Trace;
import org.htrace.TraceScope;

public class TracingFsShell {
  public static void main(String argv[]) throws Exception {
    Configuration conf = new Configuration();
    FsShell shell = new FsShell();
    conf.setQuietMode(false);
    shell.setConf(conf);
    int res = 0;
//    SpanReceiverHost.init(new HdfsConfiguration());
    TraceScope ts = null;
    try {
      ts = Trace.startSpan("FsShell", Sampler.ALWAYS);
      res = ToolRunner.run(shell, argv);
    } finally {
      shell.close();
      if (ts != null) ts.close();
    }
    System.exit(res);
  }
}
```
在eclipse中打包成jar, 然后执行
```
hadoop jar tools-config-viewer.jar TracingFsShell -mkdir /user/
```
就可以在trace.file中查看数据了
```
$ cat trace.file
{"TraceID":-2347736231853287202,"SpanID":7466649181964943895,"ParentID":-3256224216190055855,"ProcessID":"MyHdfsClient","Start":1482807799977,"Stop":1482807799978,"Description":"org.apache.hadoop.hdfs.protocol.ClientProtocol.getFileInfo","KVAnnotations":{},"TLAnnotations":[]}
{"TraceID":-2347736231853287202,"SpanID":1707104751303141757,"ParentID":-1992994217361320574,"ProcessID":"MyHdfsClient","Start":1482807800000,"Stop":1482807800006,"Description":"org.apache.hadoop.hdfs.protocol.ClientProtocol.getFileInfo","KVAnnotations":{},"TLAnnotations":[]}
{"TraceID":-2347736231853287202,"SpanID":2583422285247209579,"ParentID":819878794583469422,"ProcessID":"MyHdfsClient","Start":1482807800029,"Stop":1482807800047,"Description":"org.apache.hadoop.hdfs.protocol.ClientProtocol.mkdirs","KVAnnotations":{},"TLAnnotations":[]}

```