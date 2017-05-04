编写了MapReduce程序后, 为了确保代码运行正常, 测试是必不可少的. Hadoop的mapreduce程序如何编写TestCase呢 ?
pom.xml
```
<dependency>
    <groupId>org.apache.mrunit</groupId>
    <artifactId>mrunit</artifactId>
    <version>1.1.0</version>
    <classifier>hadoop2</classifier>
</dependency>
```
code
```
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Test;;

public class TokenizerMapperTest {

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

	  @Test
	 public void testMapper() throws IOException{

		TokenizerMapper mapper = new TokenizerMapper();
		MapDriver driver=new MapDriver(mapper);

		String text="hello world goodbye world hello hadoop goodbye hadoop";
		driver.withInput(new IntWritable(), new Text(text))
		.withOutput(new Text("hello"),new IntWritable(1))
		.withOutput(new Text("world"),new IntWritable(1))
		.withOutput(new Text("goodbye"),new IntWritable(1))
		.withOutput(new Text("world"),new IntWritable(1))
		.withOutput(new Text("hello"),new IntWritable(1))
		.withOutput(new Text("hadoop"),new IntWritable(1))
		.withOutput(new Text("goodbye"),new IntWritable(1))
		.withOutput(new Text("hadoop"),new IntWritable(1)).runTest();

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

	  @Test
	  public void testReducer() throws IOException{
		  IntSumReducer reducer = new IntSumReducer();
		  ReduceDriver driver = new ReduceDriver(reducer);
		  List<IntWritable> values = new ArrayList<IntWritable>();
		     values.add(new IntWritable(1));
		     values.add(new IntWritable(1));

		     driver.withInput(new Text("6"),values)
		     .withOutput(new Text("6"),new IntWritable(2));

		     driver.runTest();
	  }
}
```

在代码中定义了两个类, 一个mapper, 一个reducer.
