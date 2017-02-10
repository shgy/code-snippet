-- 导出hive 数据到hdfs
 insert overwrite directory '/user/me/aa.dat'
  row format delimited fields terminated by '\t'
  select * from default.my_table;

-- 新集群

create table default.my_table(
   line string
) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';

load data inpath '/user/me/aa.dat' into table default.my_table;
