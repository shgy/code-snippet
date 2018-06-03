worldcount是最简单的map-reduce程序。 那就从最简单的开始， 改造的地方是InputFormat.

map-reduce的日志如下
当input中只有1个文件时:
2018-06-03 13:38:28 INFO  JobSubmitter:494 - number of splits:1
2018-06-03 13:38:29 DEBUG LocalJobRunner:397 - Map tasks to process: 1

input目录中2个文件时：
Shuffled Maps =2
2018-06-03 14:14:39 DEBUG LocalJobRunner:397 - Map tasks to process: 2
2018-06-03 14:14:38 INFO  JobSubmitter:494 - number of splits:2


也就是说， map task的个数，跟splits的个数有关， 那么我们自定义InputFormat。

第一步的工作是搭建框架，就是实现一个自定义的InputFormat，能跑通流程即可。至于具体的功能，可以再填充。
关键的地方在于把架子搭起来。 


step1 改造wordcount, 创建EsInputFormat类， 代码如下:
```
public class EsInputFormat extends FileInputFormat {

    private Logger LOG = LoggerFactory.getLogger(EsInputFormat.class);

    @Override
    public List<InputSplit> getSplits(JobContext job) throws IOException {
        LOG.info("call getSplits method...");
        List<InputSplit> splits = new ArrayList<>();
        for(int i=0; i<10; i++){
            splits.add(new EsSplit(""+i));
        }
        return splits;
    }

    @Override
    public RecordReader createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
        LOG.info("call createRecordReader method...");
        return new EsReader();
    }
}
```
由于map任务的个数跟InputSplit的个数是强相关的，因此要重写`getSplits`方法。 这里伪造了10个EsSplit， 即我们希望有10个map任务来并行执行这一任务。

EsSplit的实现如下:
```

class EsSplit extends InputSplit implements Writable {

    private SplitLocationInfo info;

    public EsSplit(){}

    public EsSplit(String shard){
        info = new SplitLocationInfo(shard,true);
    }

    @Override
    public long getLength() throws IOException, InterruptedException {
        return 0;
    }

    @Override
    public String[] getLocations() throws IOException, InterruptedException {
        return new String[]{info.getLocation()};
    }

    @Override
    public SplitLocationInfo[] getLocationInfo() throws IOException {
        return new SplitLocationInfo[]{info};
    }

    @Override
    public void write(DataOutput out) throws IOException {
        Text.writeString(out,info.getLocation());

    }

    @Override
    public void readFields(DataInput in) throws IOException {
        info = new SplitLocationInfo(Text.readString(in),true);
    }
}

```
为什么要实现Writable接口呢？ 
hadoop集群中，map任务执行在不同的机器， 那么map任务分发的信息如何告知各个任务呢？ 
即进程间如何通信呢？ map-reduce的做法是文件。map-reduce底层依赖hdfs， 那么通过hdfs传递信息就是顺其自然的事情了。

读取的数据也不再是从文件中读取，而是从ES中读取，因此要创建ESReader类。EsReader并没有真正读取ES的数据，这里用最简单的方法，数组来做了一个模拟。
这也是一个赝品。
```

class EsReader extends RecordReader<LongWritable, Text> {
    private int index=0;
    private String[] data = {"hello","elasticsearch"};

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {

    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        return index < data.length;
    }

    @Override
    public LongWritable getCurrentKey() throws IOException, InterruptedException {
        return new LongWritable(index);
    }

    @Override
    public Text getCurrentValue() throws IOException, InterruptedException {
        return new Text(data[index++]);

    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return index/data.length;
    }

    @Override
    public void close() throws IOException {

    }
}

```

step 2: 修改job的InputFormat类
```

        LOG.info("info in main, start word count");

        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("Usage: wordcount <in> [<in>...] <out>");
            System.exit(2);
        }
        Job job = Job.getInstance(conf, "word count");
        job.setJarByClass(WordCount.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(IntSumReducer.class);
        job.setReducerClass(IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setInputFormatClass(EsInputFormat.class);
//        for (int i = 0; i < otherArgs.length - 1; ++i) {
//            FileInputFormat.addInputPath(job, new Path(otherArgs[i]));
//        }
        FileOutputFormat.setOutputPath(job,
                new Path(otherArgs[otherArgs.length - 1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
```

这样的话， 整个框架就搭建好了，接下来就是内部装修了。相关代码参考`lesson_01_code`. 本地就能直接跑这个map-reduce任务。
















