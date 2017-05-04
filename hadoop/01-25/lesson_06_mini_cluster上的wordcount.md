看到hadoop的很多测试安全都是使用MiniCluster来实现的, 因此想到worldcount应该也可以运行在MiniCluster上.
验证成功, 贴代码
```
package wordcount;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.yarn.server.MiniYARNCluster;

public class MiniClusterDemo {
	
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
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf =new Configuration();
		conf.set("fs.file.impl", "org.apache.hadoop.fs.LocalFileSystem");
	    MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf)
                .numDataNodes(DFSConfigKeys.DFS_REPLICATION_DEFAULT + 1)
                .build();
		cluster.waitActive();
		FileSystem fs = cluster.getFileSystem();
		Path dir1 = new Path("/test_dir");
	     fs.mkdirs(dir1);
	     FSDataOutputStream stm = fs.create(new Path("/test_dir/hello.dat"), true, fs.getConf()
	    	        .getInt(CommonConfigurationKeys.IO_FILE_BUFFER_SIZE_KEY, 4096),
	    	        (short) 1, 8192);
	     stm.write("hello world".getBytes());
	     stm.close();
	     
		MiniYARNCluster yrCluster =new MiniYARNCluster("test",1,1,1);
		yrCluster.init(conf);
		yrCluster.start();
		
		
		
		Job job = Job.getInstance(conf);
		job.setMapperClass(MiniClusterDemo.TokenizerMapper.class);
		job.setCombinerClass(MiniClusterDemo.IntSumReducer.class);
		job.setReducerClass(MiniClusterDemo.IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		FileInputFormat.addInputPath(job, dir1);
		FileOutputFormat.setOutputPath(job, new Path("/test_dir/out"));
		job.waitForCompletion(true);
		
		// 读取输出
		RemoteIterator<LocatedFileStatus> iter = fs.listFiles(new Path("/test_dir/out"), false);
		
		while(iter.hasNext()){
			LocatedFileStatus stat = iter.next();
			System.out.println(stat.getPath());
			Path out = stat.getPath();
			fs.copyToLocalFile(out, new Path("file:///home/shgy/tmp/"+out.getName()));
		}
			
//		System.in.read();
	}
}

```
