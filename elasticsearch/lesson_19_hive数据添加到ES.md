hive数据添加到ES的应用场景还是比较常见的。

具体的步骤如下：
1. 将elasticsearch-hadoop-version.jar添加到hive
```
ADD JAR /path/elasticsearch-hadoop.jar;
```

2. 创建Hive表：
```
CREATE EXTERNAL TABLE elastic_table(column_list)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource'='index/type’,
'es.nodes'='serverIP:port',
'es.index.auto.create'='TRUE');
INSERT OVERWRITE TABLE elastic_table 
SELECT * FROM hive_table;
```

3. 添加数据
```
-- insert data to Elasticsearch from another table called 'source'
INSERT OVERWRITE TABLE artists
    SELECT NULL, s.name, named_struct('url', s.url, 'picture', s.picture)
                    FROM source s;
```

这使用的是什么原理呢？
rest

