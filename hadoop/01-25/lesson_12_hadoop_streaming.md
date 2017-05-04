```
/opt/hadoop-2.6.0$$ ./bin/hadoop jar share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar  \
-input /home/shgy/tmp/input/bb.txt  \
-output /home/shgy/tmp/output  \
-mapper 'cut -f 2 -d,'  \
-reducer 'uniq'
```

hadoop streaming使得hadoop不仅仅是Java程序员的处理大数据利器, 对于非Java程序员, 也能利用Hadoop进行数据处理的工作.

hadoop 执行python脚本
```
$cat mapper.py
#!/usr/bin/env python
import sys

for line in sys.stdin:
    print len(line)

$hadoop jar /opt/hadoop-2.6.0/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar -input /home/shgy/tmp/input/bb.txt -output /home/shgy/tmp/output -mapper 'mapper.py' -file mapper.py
```


如果希望学习hadoop streaming的工作原理, 可以从这里入手.
```
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.streaming.StreamJob;

public class StreamingLearn {
	public static void main(String[] args) throws Exception {
		
		Configuration conf =new Configuration();
//		conf.set("fs.file.impl", "org.apache.hadoop.fs.LocalFileSystem");
	    MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf)
                .numDataNodes(DFSConfigKeys.DFS_REPLICATION_DEFAULT + 1)
                .build();
		cluster.waitActive();
		
		StreamJob job = new StreamJob();
		job.setConf(conf);
		args = new String[]{
				"-input","file:///home/shgy/tmp/input/bb.txt",
				"-output","file:///home/shgy/tmp/output",
				"-mapper","cut -f 2 -d,",
				"-reducer","uniq"
			};
		job.run(args);
		cluster.shutdown();
	}
}
```



