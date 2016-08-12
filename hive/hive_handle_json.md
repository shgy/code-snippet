使用Hive可以对海量的JSON数据进行快速处理和分析.处理的流程很简单, 只需要3步, 只需要3步, 只需要3步.

step 1: 使用hue创建Hive的表
```
CREATE TABLE if not EXISTS default.my_json_table (
           log_json string
)
```
step 2: 将数据导入到Hive中去
```
hive -e "load data inpath '/data/path/log_data' overwrite into table default.my_json_table"
```
如果数据在本地
```
hive -e "load local data inpath '/data/path/log_data' overwrite into table default.my_json_table"
```
step 3: 使用Hive的语句HQL查询即可
```
SELECT get_json_object(log_json,"$.outer.inner") from ups.ms2server_log limit 1;
```
关键在于get_json_object()函数的使用. 
需要注意的是: Hive内置的函数无法处理JSON Array, 如果需求比较复杂, 需要自己写UDF函数. 当然, 也可以使用字符串正则来处理.
