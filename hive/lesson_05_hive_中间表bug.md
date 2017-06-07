多个中间表 取相同的数据源， 会出现问题。
set hive.auto.convert.sortmerge.join=false;