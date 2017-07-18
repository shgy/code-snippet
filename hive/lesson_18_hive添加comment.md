
ALTER TABLE temporarydb.tmp_sales_0714 SET TBLPROPERTIES ('comment' = 'Hello World!');

这项功能的意义在于数据的规范。 可以开发一个Hive库/Hive表管理的小模块。
模块的功能为：
1. 查看 库/表/字段的comment。
2. 修改 库/表/字段的comment。