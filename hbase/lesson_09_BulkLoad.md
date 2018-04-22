
处理脚本如下：

```

#!/bin/bash

hadoop jar bulkload.jar com.shgy.hbase.HFileGenerator zkhost1,zkhost2,zkhost3 hbase_tab \

3 \

/user/hive/warehouse/input1 \

/user/hive/warehouse/input2 \

/user/hive/warehouse/input3 \

/user/hive/warehouse/input4 \

/user/hive/warehouse/input5 \

/user/hive/bulk/output

```

核心类如下：

```

package com.shgy.hbase;



import java.io.IOException;

import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;

import java.util.Iterator;

import java.util.Map;

import java.util.Map.Entry;



import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.FileSystem;

import org.apache.hadoop.fs.Path;

import org.apache.hadoop.hbase.HColumnDescriptor;

import org.apache.hadoop.hbase.HTableDescriptor;

import org.apache.hadoop.hbase.KeyValue;

import org.apache.hadoop.hbase.TableName;

import org.apache.hadoop.hbase.client.HBaseAdmin;

import org.apache.hadoop.hbase.client.HTable;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;

import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat;

import org.apache.hadoop.hbase.mapreduce.KeyValueSortReducer;

import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;

import org.apache.hadoop.hbase.mapreduce.SimpleTotalOrderPartitioner;

import org.apache.hadoop.hbase.util.Bytes;

import org.apache.hadoop.hbase.util.RegionSplitter;

import org.apache.hadoop.io.LongWritable;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.mapreduce.Mapper;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import org.codehaus.jackson.map.ObjectMapper;





public class HFileGenerator {

	

	private static final ObjectMapper objectMapper = new ObjectMapper();

	

	public static void createTable(Configuration conf, String table) {

		byte[][] splitKeys = new RegionSplitter.HexStringSplit().split(100);



		HBaseAdmin admin = null;

		try {

			admin = new HBaseAdmin(conf);



			TableName tableName = TableName.valueOf(table);



			if (admin.tableExists(tableName)) {

				try {

					admin.disableTable(tableName);

				} catch (Exception e) {

				}

				admin.deleteTable(tableName);

			}



			HTableDescriptor tableDesc = new HTableDescriptor(tableName);

			HColumnDescriptor columnDesc = new HColumnDescriptor(

					Bytes.toBytes("cf"));

			columnDesc.setMaxVersions(1);

			tableDesc.addFamily(columnDesc);



			admin.createTable(tableDesc, splitKeys);

		} catch (IOException e1) {

			e1.printStackTrace();

		} finally {

			try {

				if (null != admin) {

					admin.close();

				}

			} catch (IOException e) {

				e.printStackTrace();

			}

		}



	}

	

	public static boolean process(String[] args) throws IOException,

	InterruptedException, ClassNotFoundException {

		

		if(args.length < 4){

			System.out.println("Usage <zkHost>  <htable>  <inputPathCount>  <inputPath...>  <outPath>");

			System.exit(1);

		}

		

		String zkHost = args[0] ; 

		String htable = args[1] ;

		int inputPathCount = Integer.parseInt(args[2]);

		String [] inputPaths = new String[inputPathCount];

		for(int i=0;i<inputPaths.length;i++){

			inputPaths[i] = args[i+3];

		}

		String outPath = args[inputPathCount+3];

		

		

		Configuration conf = new Configuration();

		conf.set("hbase.zookeeper.quorum", zkHost);

		

		FileSystem fs = FileSystem.get(conf);



		Job job = new Job(conf, "HbaseImport");

		job.setJarByClass(HFileGenerator.class);



		job.setMapperClass(HFileMapper.class);

		job.setReducerClass(KeyValueSortReducer.class);



		job.setMapOutputKeyClass(ImmutableBytesWritable.class);

		job.setMapOutputValueClass(KeyValue.class);



		job.setPartitionerClass(SimpleTotalOrderPartitioner.class);

		

		for(String inputPath: inputPaths){

			FileInputFormat.addInputPath(job, new Path(inputPath));

		}

		

		FileOutputFormat.setOutputPath(job, new Path(outPath));

		createTable(conf, htable);

		HTable table = new HTable(conf, htable);

		HFileOutputFormat.configureIncrementalLoad(job, table);

		if (job.waitForCompletion(true)) {

			LoadIncrementalHFiles loader;

			try {

				loader = new LoadIncrementalHFiles(conf);

				loader.doBulkLoad(new Path(outPath), table);

			} catch (Exception e) {

				e.printStackTrace();

			}



		} else {

			return false;

		}

		

		Path path = new Path(outPath);

		fs.deleteOnExit(path);

		fs.close();

		return true;

	}



	public static void main(String[] args) throws IOException,

			InterruptedException, ClassNotFoundException {

		process(args);

	}

}

```

Mapper类

```

package com.shgy.hbase;



import java.io.IOException;

import java.util.regex.Matcher;

import java.util.regex.Pattern;



import org.apache.hadoop.hbase.KeyValue;

import org.apache.hadoop.hbase.io.ImmutableBytesWritable;

import org.apache.hadoop.hbase.util.Bytes;

import org.apache.hadoop.io.LongWritable;

import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Mapper;

import org.apache.hadoop.mapreduce.Mapper.Context;

import org.codehaus.jackson.map.ObjectMapper;





public class HFileMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, KeyValue> {

	@Override

	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

		String rk = value.toString();



		ImmutableBytesWritable rowkey = new ImmutableBytesWritable(Bytes.toBytes(rk));

		KeyValue kv = new KeyValue(Bytes.toBytes(rk), Bytes.toBytes("f1"), Bytes.toBytes("c1"), Bytes.toBytes("1"));

		context.write(rowkey, kv);



}

}

```






