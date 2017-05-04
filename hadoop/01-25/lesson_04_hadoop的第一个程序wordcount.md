通常使用hadoop, 第一个程序是wordcount. 一般的教程是启动一个伪分布式集群, 然后运行. 
但是hadoop的官方文档中, "Setting up a Single Node Cluster." 并没有启动集群.
因此, 最简单的hadoop wordcount 流程如下:
在wordcount之前, 解压hadoop-2.6.0, 然后配置HADOOP_HOME即可.

Step 1: 在eclipse中新建maven项目, 项目依赖如下
```
<dependencies>
    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-common</artifactId>
        <version>2.6.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-hdfs</artifactId>
        <version>2.6.0</version>
    </dependency>
    <dependency>
        <groupId>org.apache.hadoop</groupId>
        <artifactId>hadoop-client</artifactId>
        <version>2.6.0</version>
    </dependency>
</dependencies>
```

Step 2: 
添加log4j.properties
```
log4j.rootLogger=DEBUG,console
log4j.additivity.org.apache=true
# 控制台(console)
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.Threshold=DEBUG
log4j.appender.console.ImmediateFlush=true
log4j.appender.console.Target=System.err
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=[%-5p] %d(%r) --> [%t] %l: %m %x %n
```

Step 3: wordcount代码
```
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, IntWritable>{

    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
      StringTokenizer itr = new StringTokenizer(value.toString());
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        context.write(word, one);
      }
    }
  }

  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(WordCount.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
```

eclipse运行参数: `file:///home/shgy/tmp/input file:///home/shgy/tmp/output`


升级版: 使用MiniCluster快速配置Hadoop开发环境

Step 1: 编译hadoop源码:
```
mvn clean package -Pdist,native -DskipTests -Dtar
mvn eclipse:eclipse -DskipTests
```
配置pom.xml
```
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-minicluster</artifactId>
    <version>2.6.0</version>
</dependency>
```
Step 2: 启动MiniCluster
```

Configuration configuration =new Configuration();
File tempDir = Files.createTempDir();
configuration.set(MiniDFSCluster.HDFS_MINIDFS_BASEDIR, tempDir.getAbsolutePath());
new MiniDFSCluster.Builder(configuration).build();

MiniYARNCluster yrCluster =new MiniYARNCluster("test",1,1,1);
yrCluster.init(configuration);
yrCluster.start();
System.in.read();
```


