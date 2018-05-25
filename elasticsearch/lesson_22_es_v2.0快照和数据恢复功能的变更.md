首先了解一下es的快照和数据恢复。
1. 创建path.repo
```
cat config/elasticsearch.yml 
# ------------------------------- snapshot ------------------------------------
path.repo: /data/backup
```

2. 创建快照仓库
```
curl -XPUT http://localhost:9200/_snapshot/my_backup -d '{
  "type":"fs",
  "settings": {
    "location":"/data/backup"
  }
}'

curl -XGET http://localhost:9200/_snapshot/my_backup
curl -XDELETE http://localhost:9200/_snapshot/my_backup
```
注意： `location`要在`path.repo`中

3. 备份
```
curl -XPUT http://localhost:9200/_snapshot/my_backup/b20180525
```

4. 查看备份
```
curl -XGET http://localhost:9200/_snapshot/my_backup/b20180525?pretty
{
  "snapshots" : [ {
    "snapshot" : "b20180525",
    "version_id" : 2010199,
    "version" : "2.1.1",
    "indices" : [ "twitter", "template_test", "parentchild", "hbaseindex=20180427234847" ],
    "state" : "SUCCESS",
    "start_time" : "2018-05-25T15:44:32.292Z",
    "start_time_in_millis" : 1527263072292,
    "end_time" : "2018-05-25T15:44:32.649Z",
    "end_time_in_millis" : 1527263072649,
    "duration_in_millis" : 357,
    "failures" : [ ],
    "shards" : {
      "total" : 8,
      "failed" : 0,
      "successful" : 8
    }
  } ]
}

```

5. 还原
```
curl -XPOST http://localhost:9200/_snapshot/my_backup/b20180525/_restore -d '{
  "indices":"twitter"
}'

```
注意：还原索引只能还原关闭的索引。处于open状态的索引是不能还原的。


