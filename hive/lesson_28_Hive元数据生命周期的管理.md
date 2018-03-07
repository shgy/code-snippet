一些hive表过期后, 需要定期清除。 我们需要获取的信息就是Hive表的最后访问时间。
用到的命令

```
show table extended in database_name like table_name;
```
结果如下：
```
1   tableName: ***
2	owner: ***
3	location:hdfs:// ***
4	inputformat:org.apache.hadoop.hive.ql.io.orc.OrcInputFormat
5	outputformat:org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat
6	columns:struct columns {*}
7	partitioned:false
8	partitionColumns:
9	totalNumberFiles:12
10	totalFileSize:32767443
11	maxFileSize:8900195
12	minFileSize:49
13	lastAccessTime:1519890634584
14	lastUpdateTime:1519890635410
```

使用hive的udf就能看到该表的最后访问时间：
```
select from_unixtime(1519890634)
```