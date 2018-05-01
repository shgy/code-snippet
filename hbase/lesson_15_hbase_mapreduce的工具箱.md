Hbase-mapreduce的文档比较少. 而且由于版本变化比较快, 因此代码是了解功能极好的入口. 那么学习hbase-mapreduce, 从哪里的代码入手呢?
参考hbase reference guide的chapter 47的hbase-server-1.1.0.jar
```
$ ${HADOOP_HOME}/bin/hadoop jar ${HBASE_HOME}/hbase-server-VERSION.jar
An example program must be given as the first argument.
Valid program names are:
 copytable: Export a table from local cluster to peer cluster
 completebulkload: Complete a bulk data load.
 export: Write table data to HDFS.
 import: Import data written by Export.
 importtsv: Import data in TSV format.
 rowcounter: Count rows in HBase table
```
一共有6个样列,分别是`rowcounter`,`importtsv`, `import`, `export`,`completebulkload`, `copytable` .
比如, 我希望统计hbase表的行数, 可以使用如下的命令:
```
HADOOP_CLASSPATH=$(hbase mapredcp):/opt/hbase-1.1.0/conf hadoop jar /opt/hbase-1.1.0/lib/hbase-server-1.1.0.jar rowcounter test
```

