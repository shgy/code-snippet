将hbase一个表的数据复制到另一张表. 这个需求的应用场景很少. 学习这个有个很重要的原因在于:
这个里面有个实现: 直接写入数据到Hbase表. 大批量的数据, 可以用BulkLoad来解决. 小量的数据, 直接往Hbase中写就是了.

代码的实现很简单:
```
package com.shgy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.Import;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;


public class HbaseTableCopy {

    public static void main(String... args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.property.clientPort", "2181");
        config.set("hbase.zookeeper.quorum","localhost");
        Job job = new Job(config, "ExampleRead");
        job.setJarByClass(HbaseTableCopy.class);     // class that contains mapper

        Scan scan = new Scan();
        scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
        scan.setCacheBlocks(false);  // don't set to true for MR jobs
        // set other scan attrs

        TableMapReduceUtil.initTableMapperJob(
                "test",        // input HBase table name
                scan,             // Scan instance to control CF and attribute selection
                Import.Importer.class,   // mapper
                null,             // mapper output key
                null,             // mapper output value
                job);

        TableMapReduceUtil.initTableReducerJob("test2",null,job);
        job.setNumReduceTasks(0);

        boolean b = job.waitForCompletion(true);
        if (!b) {
            throw new IOException("error with job!");
        }
    }
}
```
基本没有自己的代码, 都是相关jar包集成的功能. `Import.Importer.class`. 好了, 假如我们需要对数据针对业务做一些处理,
比如过滤含有某些敏感字符的row. 那就不能直接用Import.Importer.class, 得自己写代码了.

```
package com.shgy;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.Import;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;


public class HbaseTableCopy {
    public static class MyMapper extends TableMapper<ImmutableBytesWritable, Mutation> {

        public void map(ImmutableBytesWritable row, Result value, Context context) throws IOException, InterruptedException {
            // this example is just copying the data from the source table...
            context.write(row, resultToPut(row,value));
        }

        private static Put resultToPut(ImmutableBytesWritable key, Result result) throws IOException {
            Put put = new Put(key.get());
            for (Cell kv : result.rawCells()) {
                put.add(kv);
            }
            return put;
        }
    }

    public static void main(String... args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.property.clientPort", "2181");
        config.set("hbase.zookeeper.quorum","localhost");
        //Add any necessary configuration files (hbase-site.xml, core-site.xml)
//        config.addResource(new Path("hbase-site.xml"));
        Job job = new Job(config, "ExampleRead");
        job.setJarByClass(HbaseTableCopy.class);     // class that contains mapper

        Scan scan = new Scan();
        scan.setCaching(500);        // 1 is the default in Scan, which will be bad for MapReduce jobs
        scan.setCacheBlocks(false);  // don't set to true for MR jobs
// set other scan attrs

        TableMapReduceUtil.initTableMapperJob(
                "test",        // input HBase table name
                scan,             // Scan instance to control CF and attribute selection
                MyMapper.class,   // mapper
                null,             // mapper output key
                null,             // mapper output value
                job);

        TableMapReduceUtil.initTableReducerJob("test3",null,job);
        job.setNumReduceTasks(0);

        boolean b = job.waitForCompletion(true);
        if (!b) {
            throw new IOException("error with job!");
        }
    }
}
```



