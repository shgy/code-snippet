package self.partitioner;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/*
 *  hadoop in action: 统计从每个机场出境的人数的例子感觉有些牵强. 搜索到一篇博客(http://www.tuicool.com/articles/nURba2),
 *  觉得例子扩展了我对Hadoop Partitioner的认知. 如果 hadoop in action中, 将需求修改为按出境机场归档数据集可能更合适一些.
 *
 *  需求如下: 数据为手机上网日志. 将数据中手机号正确和手机号不正确的数据分开. 分别存储到两个partition文件中.
$ cat /home/shgy/input/log.dat
1363157993044 18211575961
1363157993044 18211575968
1363157993044 18211575962
1363157493042 18211575963
1363157993044 18211573961
1363157993044 18211572961
1363157993044 1821157596
1363157993064 18211579961
1363157593044 18211574961
得到的结果如下:
$ tree output1
output1
├── part-r-00000
├── part-r-00001
└── _SUCCESS

0 directories, 3 files
$ cat output1/part-r-00000
1363157993044 18211572961
1363157993044 18211573961
1363157593044 18211574961
1363157993044 18211575961
1363157993044 18211575962
1363157493042 18211575963
1363157993044 18211575968
1363157993064 18211579961
$ cat output1/part-r-00001
1363157993044 1821157596

可以看到, 一个文件中全部是正确的手机号, 另一个文件是错误的手机号.
 * */

public class PhoneSplit {
	 static class MyMapper extends Mapper<Object, Text, Text, Text>{

       private Text phone = new Text();
	   public void map(Object key, Text value, Context context
	                   ) throws IOException, InterruptedException {
		   String p = value.toString().split(" ")[1];
		   phone.set(p);
		   context.write(phone, value);
	   }
	 }
	 static class MyPartitioner extends Partitioner<Text, Text> {
		@Override
		public int getPartition(Text key, Text value, int numPartitions) {
			// 实现不同的长度不同的号码分配到不同的reduce task中
			int numLength = key.toString().length();
			return numLength == 11 ? 1: 0 ;
		}
	}
   static class MyReducer extends Reducer<Text,Text,Text,Text> {

      public void reduce(Text key, Iterable<Text> values,
                         Context context
                         ) throws IOException, InterruptedException {
        for (Text val : values) {
            context.write(null, val);
        }
      }
   }
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

		Configuration conf =new Configuration();
	    MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).build();
		cluster.waitActive();

		Job job = Job.getInstance(conf);
		job.setMapperClass(PhoneSplit.MyMapper.class);
		job.setReducerClass(PhoneSplit.MyReducer.class);
		job.setPartitionerClass(PhoneSplit.MyPartitioner.class);
		job.setNumReduceTasks(2); // 这里的设置很重要, 如果不设置为2, 则不会出现上面的结果.
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		FileInputFormat.addInputPath(job, new Path("file:///home/shgy/tmp/input1"));
		FileOutputFormat.setOutputPath(job, new Path("file:///home/shgy/tmp/output1"));
		job.waitForCompletion(true);

		cluster.shutdown();
	}
}
