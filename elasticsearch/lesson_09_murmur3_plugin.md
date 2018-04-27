虽然es有aggregations功能, 但是海量数据实时处理, 依然耗时. 唯一的办法是空间换时间. 这里介绍一款插件:mapper-murmur3. 用于优化聚合功能.
原理先放放, 看看使用方法.
```
sudo bin/plugin install mapper-murmur3
```

如何检测插件已经安装好了呢?
```
$ curl 'localhost:9200/_cat/plugins?v'
name      component      version type url            
El Aguila head           master  s    /_plugin/head/ 
El Aguila kopf           2.1.2   s    /_plugin/kopf/ 
El Aguila mapper-murmur3 2.1.1   j            
```

参考:
https://www.elastic.co/guide/en/elasticsearch/plugins/2.1/mapper-murmur3.html

