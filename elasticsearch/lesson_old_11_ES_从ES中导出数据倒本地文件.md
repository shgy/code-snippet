应用场景：将es中的数据导出到本地文件中。

```
//EsDataProcess.java
public static void es_index_to_file() throws IOException, ClassNotFoundException, InterruptedException{

		Configuration conf = new Configuration();

		conf.set("es.nodes", "ubuntu-shgy:9200");

		conf.set("es.resource", "dp_test/qyxx");

//		conf.set("es.resource.write", "dp_test_2/qyxx");

//		conf.set("es.mapping.id", "BBD_QYXX_ID");

		conf.set("es.field.read.empty.as.null", "no");

		Job job = Job.getInstance(conf);

		job.setInputFormatClass(EsInputFormat.class);

//		job.setOutputFormatClass(EsOutputFormat.class);

		job.setMapperClass(IndexToFileMapper.class);

		job.setMapOutputKeyClass(Text.class);

		job.setMapOutputValueClass(Text.class);

		FileOutputFormat.setOutputPath(job,new Path("/home/shgy/output/"));

		job.waitForCompletion(true);

	}

```
```

import java.io.IOException;



import org.apache.hadoop.io.MapWritable;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Mapper;

import org.codehaus.jettison.json.JSONObject;



public class IndexToFileMapper extends  Mapper<Text,MapWritable , Text, Text> {

 @Override

 public void map(Text key, MapWritable value,Context context) throws IOException, InterruptedException {

	 

	 JSONObject doc = new JSONObject(value);

	 Text text = new Text(doc.toString());

	 context.write(new Text(""), text);

 }

}

```
