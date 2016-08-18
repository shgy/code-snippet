由于Hive比较慢,因此一般多用于定时的统计任务.比如每天跑一次这种.
然而开发中需要测试,每天不只跑一次. 因此需要一种老数据的自动覆盖机制.
可以按照如下的方式建表
```
create table if not exists  mydatabase.mytable(
 myvar string
)
PARTITIONED by(dt string)
STORED as ORC;
```
然后插入数据的时候,就
```
INSERT overwrite TABLE mydatabase.mytable PARTITION (dt)
```


