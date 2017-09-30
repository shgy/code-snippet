MapReduce的Job有两种工作模式: Local和Yarn， 分别对应这 `LocalJobRunner`和`YARNRunner`。其中， local模式用于本地调试。

通常， 我们的Mapper和Reducer实现好后， 出于某些原因， 需要调试代码的逻辑， local模式就很方便了。
从hdfs上下载部分输入的样例数据， 就可以在本地运行MapReduce程序。 不用考虑集群等各种复杂的环境。

换言之， 学习mapreduce的内部细节， 可以先略过Yarn这一块的知识点。在Local模式下， 把各个细节弄清除了，再延伸到集群模式。
不失为一条比较平坦的学习曲线。


```java
package wordcount;
import java.io.File;
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

  private static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      //递归删除目录中的子目录下
      for (int i=0; i<children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }
    // 目录此时为空，可以删除
    return dir.delete();
  }

  public static void main(String[] args) throws Exception {

    deleteDir(new File("/home/shgy/tmp/out"));
    args = new String[]{"/home/shgy/tmp/input", "/home/shgy/tmp/out"};

    Configuration conf = new Configuration();
//    conf.set("fs.defaultFS", "hdfs://localhost:9000");
    Job job = Job.getInstance(conf, "word count");
    job.setJarByClass(wordcount.WordCount.class);
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




参考：http://blog.csdn.net/lipeng_bigdata/article/details/51285514