BulkLoad可谓是Hbase的秘密武器了, 在海量数据的处理上, 这个功能的应用频率还是相当高的.

这里为了演示其功能, 前期就做了一些准备.
1. 在hbase中创建好了表:
```
create 'test4','f1'
```
2. 准备好了样例数据. 这里只存储rowkey, value全部一样. 这个典型的应用场景是判断rk是否存在. 比如爬虫开发中, 某个网页是否已经抓取. 
```
$ cat bulkinput/*
row1
row2
row3
row4
row5
```

3. 相关的代码如下:
```
package com.shgy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;


public class HbaseBulkloadDemo {
    public static class HFileMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, KeyValue> {

        @Override

        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            String rk = value.toString();


            ImmutableBytesWritable rowkey = new ImmutableBytesWritable(Bytes.toBytes(rk));

            KeyValue kv = new KeyValue(Bytes.toBytes(rk), Bytes.toBytes("f1"), Bytes.toBytes("c1"), Bytes.toBytes("1"));

            context.write(rowkey, kv);


        }
    }

        public static void main(String... args) throws IOException, ClassNotFoundException, InterruptedException {
            Configuration conf = HBaseConfiguration.create();
            conf.set("hbase.zookeeper.property.clientPort", "2181");
            conf.set("hbase.zookeeper.quorum","localhost");
            //Add any necessary configuration files (hbase-site.xml, core-site.xml)
    //        config.addResource(new Path("hbase-site.xml"));
            Job job = new Job(conf, "HbaseImport");
            job.setJarByClass(HbaseBulkloadDemo.class);     // class that contains mapper

            job.setMapperClass(HFileMapper.class);
            job.setReducerClass(KeyValueSortReducer.class);

            job.setMapOutputKeyClass(ImmutableBytesWritable.class);
            job.setMapOutputValueClass(KeyValue.class);

            job.setPartitionerClass(SimpleTotalOrderPartitioner.class);

            FileInputFormat.addInputPath(job, new Path("/home/shgy/tmp/bulkinput/"));
            FileOutputFormat.setOutputPath(job, new Path("/home/shgy/tmp/bulkoutput/"));

            HTable table = new HTable(conf, "test4");

            HFileOutputFormat.configureIncrementalLoad(job, table);
            boolean b = job.waitForCompletion(true);
            if (!b) {
                throw new IOException("error with job!");
            }else{
                LoadIncrementalHFiles loader;

                try {

                    loader = new LoadIncrementalHFiles(conf);

                    loader.doBulkLoad(new Path("/home/shgy/tmp/bulkoutput/"), table);

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }
        }
}
```
这个例子只是为了展示Bulkload的用法, 还有一些扫尾的工作,比如HFile文件的删除都是没有做的.


