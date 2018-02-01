功能: 忽略表头, 自动处理csv的 引号, 逗号 问题
drop table if exists temporarydb.tmp_sgy_0104_d1;
create table if not exists temporarydb.tmp_sgy_0104_d1
(
   f1 string,
   f2 string,
   f3 string,
   f4 string,
   f5 string,
   f6 string,
   f7 string,
   f8 string,
   f9 string
)ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.OpenCSVSerde'
TBLPROPERTIES ("skip.header.line.count"="1");
load data inpath '/user/guangying/a.txt' overwrite into table  temporarydb.tmp_sgy_0104_d1;

select *from temporarydb.tmp_sgy_0104_d1;

参考:
https://stackoverflow.com/questions/20813355/skip-first-line-of-csv-while-loading-in-hive-table
