通常我们希望一些系统参数能够动态调整。 在ES的使用过程中，最容易记住的动态调整的参数应该就是索引的副本数量： `number_of_replicas`

1. 查看参数：
```
curl -XGET localhost:9200/_cluster/settings`
curl -XGET 'http://localhost:9200/twitter-01/_settings?name=index.number_*'
```

2. 添加参数

3. 修改参数
```
curl -XPUT 'http://localhost:9200/twitter/_settings' -d '{
    "index" : {
        "custom_bitmap_setting" : 2,
    }
}'
```

3. 在代码中使用参数
