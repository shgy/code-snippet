Hive的DDL(Data Define Language)与MySQL很像.

记录一些常用的
1. 修改表名
```
alter table if exists default.test3 rename to default.tab1;
```
1. 复制表结构
```
create table  if not exists default.test4 like default.test3;
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

4. Hive常用的设置参数
```
set hive.exec.mode.local.auto=true;
```
对于单机伪分布式的环境,必须要设置此值,Hive才能正常运行.

```
set hive.execution.engine=tez;
```
hive的执行引擎, tez比mr要快很多.

