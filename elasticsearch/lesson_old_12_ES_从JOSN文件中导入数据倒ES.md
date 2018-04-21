应用场景：将es中的数据导出到本地文件中。

```
//EsDataProcess.java
public static void es_index_to_file() throws IOException, ClassNotFoundException, InterruptedException{


		Configuration conf = new Configuration();

		conf.set("es.nodes", "ubuntu-shgy:9200");

//		conf.set("es.resource", "dp_test/qyxx");

		conf.set("es.resource.write", "dp_test_3/qyxx");

		conf.set("es.input.json", "yes");

		conf.set("es.mapping.id", "BBD_QYXX_ID");

		conf.set("es.field.read.empty.as.null", "no");

		Job job = Job.getInstance(conf);

//		job.setInputFormatClass(EsInputFormat.class);

		job.setOutputFormatClass(EsOutputFormat.class);

		job.setMapperClass(FileToIndexMapper.class);

		job.setMapOutputKeyClass(LongWritable.class);

		job.setMapOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job,new Path("hdfs://localhost:9000/es/input/"));

		job.waitForCompletion(true);

	}

```
```

import java.io.IOException;



import org.apache.hadoop.io.LongWritable;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Mapper;



public class FileToIndexMapper extends  Mapper<LongWritable,Text , LongWritable, Text> {

 @Override

 public void map(LongWritable key, Text value,Context context) throws IOException, InterruptedException {

	 

	 Text text = new Text(value.toString());

	 context.write(key, text);

 }

}

```
