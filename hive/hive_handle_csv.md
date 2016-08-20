1 准备数据集如下:
```
$ cat tmp/dat.csv 
2015-01-01,1
2015-01-02,2
2015-02-01,3
2015-02-05,6
```
2 创建数据表
```
use default;
create table test(dt string, n int) ROW FORMAT DELIMITED FIELDS TERMINATED BY ',';
```
3 导入数据
```
load data local inpath '/home/shgy/tmp/dat.csv' overwrite into table default.test;
```
4 查询结果
```
select * from test;
```
