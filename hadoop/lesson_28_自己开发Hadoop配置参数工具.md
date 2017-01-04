学习
Hadoop平台参数众多,  虽然也有web页面提供查看参数的功能. 但是不好用. 写一个工具从命令行读取参数.
代码如下:
```
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
/*
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-common</artifactId>
    <version>2.6.0</version>
</dependency>
 */
public class HadoopConfDisplay extends Configured implements Tool{

	static {
	    Configuration.addDefaultResource("core-default.xml");
	    Configuration.addDefaultResource("yarn-default.xml");
	    Configuration.addDefaultResource("mapred-default.xml");
	    Configuration.addDefaultResource("core-site.xml");
	    Configuration.addDefaultResource("hdfs-site.xml");
	    Configuration.addDefaultResource("mapred-site.xml");
	    Configuration.addDefaultResource("yarn-site.xml");
	}

	  @Override
	  public int run(String[] args) throws Exception {
	    Configuration conf = getConf();
	    for (Entry<String, String> entry: conf) {
	      System.out.printf("%s=%s\n", entry.getKey(), entry.getValue());
	    }
	    return 0;
	  }

	public static void main(String[] args) throws Exception {
	    int exitCode = ToolRunner.run(new HadoopConfDisplay(), args);
	    System.exit(exitCode);
	}
}

```

使用eclipse默认的功能打包后, 在命令行中运行的样例如下:
```
$ hadoop jar tools-config-viewer.jar HadoopConfDisplay  | grep ubertask
mapreduce.job.ubertask.maxmaps=9
mapreduce.job.ubertask.maxreduces=1
mapreduce.job.ubertask.enable=false
```
这比
```
$ curl -s http://hcb:8042/conf | grep ubertask
<property><name>mapreduce.job.ubertask.enable</name><value>false</value><source>mapred-default.xml</source></property>
<property><name>mapreduce.job.ubertask.maxmaps</name><value>9</value><source>mapred-default.xml</source></property>
<property><name>mapreduce.job.ubertask.maxreduces</name><value>1</value><source>mapred-default.xml</source></property>
```
要清晰得多
参考: http://blog.csdn.net/beliefer/article/details/51145397

今天看<hadoop权威指南> 第5章, 上面的代码就是ToolRunner的参考样列. 看来, 作者也不厚道啊.