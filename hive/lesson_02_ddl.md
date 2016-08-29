Hive的DDL(Data Define Language)与MySQL很像.

记录一些常用的
1. 修改表名
```
alter table default.test3 rename to default.tab1;
```

2. 修改列名
```
alter table default.test3 change column `_c1` cnt int;
```
3. Timestamp的转换
在Hive的官方文档中已经说明
```
Supports traditional UNIX timestamp with optional nanosecond precision.
Supported conversions:
Integer numeric types: Interpreted as UNIX timestamp in seconds
Floating point numeric types: Interpreted as UNIX timestamp in seconds with decimal precision
Strings: JDBC compliant java.sql.Timestamp format "YYYY-MM-DD HH:MM:SS.fffffffff" (9 decimal place precision)
```
即可以将Timestamp转换成秒来计算差值

