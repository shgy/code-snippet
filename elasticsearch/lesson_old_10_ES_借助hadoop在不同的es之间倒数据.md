应用场景：同一个集群中，同一份数据应用使用不同的分词方式，需要导数据。即index --> index.

1 pom.xml配置：
```
 <dependencies>
<dependency>
 <groupId>org.elasticsearch</groupId>
 <artifactId>elasticsearch-hadoop</artifactId>
 <version>2.0.0</version>
</dependency> 
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
  <repositories>
   <repository>
    <id>sonatype-oss</id>
    <url>http://oss.sonatype.org/content/repositories/snapshots</url> 
    <snapshots><enabled>true</enabled></snapshots> 
   </repository> 
   <repository>
 <id>conjars.org</id>
 <url>http://conjars.org/repo</url>
</repository>
</repositories>

```
2 MyMapper.java
```

import java.io.IOException;



import org.apache.hadoop.io.MapWritable;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Mapper;

import org.codehaus.jettison.json.JSONObject;



public class MyMapper extends  Mapper<Text,MapWritable , Text, Text> {

 @Override

 public void map(Text key, MapWritable value,Context context) throws IOException, InterruptedException {

	 

	 JSONObject obj = new JSONObject(value);

	 Text doc = new Text(obj.toString());

	 context.write(key, doc);

 }

}

```

3 EsDataProcess.java
```

import java.io.IOException;



import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;

import org.elasticsearch.hadoop.mr.EsInputFormat;

import org.elasticsearch.hadoop.mr.EsOutputFormat;



public class ESDataProcess {

	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

		

		Configuration conf = new Configuration();

//		conf.setSpeculativeExecution(false);   

//		jobConf.setBoolean("mapred.map.tasks.speculative.execution", false);    

//		jobConf.setBoolean("mapred.reduce.tasks.speculative.execution", false);

		conf.set("es.nodes", "ubuntu-shgy:9200");

		

		conf.set("es.resource.read", "dp_test_2/qyxx");

		conf.set("es.resource.write", "dp_test_3/qyxx");

		conf.set("es.mapping.id", "BBD_QYXX_ID");

		conf.set("es.input.json", "yes");

		conf.set("es.field.read.empty.as.null", "no");

//		conf.set("es.output.json", "true");

		Job job = Job.getInstance(conf);

		job.setInputFormatClass(EsInputFormat.class);

		job.setOutputFormatClass(EsOutputFormat.class);

		job.setMapperClass(MyMapper.class);

		job.setMapOutputKeyClass(Text.class);

		job.setMapOutputValueClass(Text.class);

		job.waitForCompletion(true);

		

	}

}

```
关于`		conf.set("es.field.read.empty.as.null", "no");`这句配置，来之不易。如果没有它，那么出来的数据中，如果值为"", 就会变成"(null)"。为此还傻傻地在github中提交了一个Bug，感谢这个Bug。
直接到github中当前项目下使用关键词""搜索, 即可查看到定义各个变量的配置类org.elasticsearch.hadoop.cfg.ConfigurationOptions.java 中定义的参数.
