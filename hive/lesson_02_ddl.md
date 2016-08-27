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


