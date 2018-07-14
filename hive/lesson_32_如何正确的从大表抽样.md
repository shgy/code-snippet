通常我们做试验， 需要从数据中抽样。 通常的做法如下：

1. 使用 limit n; 
在Hive中使用这个语句是不被推荐的， 因为 create table sample_table as select * from table limit n; 在Hive中会被解析成table_scan 和 limit两个operator.
这样会全表扫描，而且有一个reduce. 对于大表是非常慢的， 主要是卡在reduce环节。

2. TABLESAMPLE (n PERCENT)
select * from table TABLESAMPLE (10 PERCENT); 取10%的数据
3. TABLESAMPLE (nM)
select * from table TABLESAMPLE (10M); 取10M的数据; 这个用于测试导入导出性能更合适。
4. TABLESAMPLE (n ROWS)
select * from table TABLESAMPLE (10 ROWS); 从每个mapper中取10条记录。加入有100个mapper,就有1000条记录。这个感觉得跑两遍， 第一遍看有多少个mapper.
5. TABLESAMPLE (BUCKET 1 OUT OF 10 ON rand())

select * from table TABLESAMPLE (BUCKET 1 OUT OF 10 ON rand()); 将整个表分成10个桶， 取第一个桶。基本是数据的10%， 但是不那么精准。

参考：
http://lxw1234.com/archives/2015/08/444.htm
