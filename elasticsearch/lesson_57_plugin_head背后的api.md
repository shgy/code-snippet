学习ES的时候，接触的第一个插件就是head, 通过该插件可以查看各个节点和索引的信息，可以视为数据可视化典型的应用。
那么head插件初始化的时候，调用了那些api呢？

```
curl 'http://localhost:9200/_cluster/state' 
curl 'http://localhost:9200/_stats' 
curl 'http://localhost:9200/_nodes/stats' 
curl 'http://localhost:9200/_nodes' 
curl 'http://localhost:9200/_cluster/health' 
```

目前，已经学习了`_cluster/state` 的实现。下面依次学习一下其他的api.
