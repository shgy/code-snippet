hive数据添加到ES的应用场景还是比较常见的,实验可行。
hive的版本： hive-1.1.0-cdh5.9.0

具体的步骤如下：
1. 将elasticsearch-hadoop-version.jar添加到hive
```
wget https://artifacts.elastic.co/downloads/elasticsearch-hadoop/elasticsearch-hadoop-6.3.0.zip
unzip elasticsearch-hadoop-6.3.0.zip
hdfs dfs -mkdir /user/test/es_hadoop/
hdfs dfs -put elasticsearch-hadoop-hive-6.3.0.jar /user/test/es_hadoop/
ADD JAR /user/test/es_hadoop//elasticsearch-hadoop.jar;
```

2. 创建Hive表：
```
CREATE EXTERNAL TABLE elastic_table(
   uuid string,
   key1 int,
   key2 int,
   day string
)
STORED BY 'org.elasticsearch.hadoop.hive.EsStorageHandler'
TBLPROPERTIES('es.resource'='index/type',
'es.nodes'='serverIP:port',
'es.index.auto.create'='TRUE',
'es.mapping.id' = 'uuid'
);
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
rest. 为了避免版本的问题，这里使用http的接口导入数据。 

这个方式的优点在于，不需要写一行代码，周期短，见效快。 缺点在于使用Http的接口，从理论上性能比直接使用TransportClient要低一些，有待实验验证。

参考:
http://note4code.com/2016/06/17/hive-%E5%90%91-elasticsearch-%E5%AF%BC%E5%87%BA%E6%95%B0%E6%8D%AE/
