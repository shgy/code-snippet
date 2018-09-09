es 索引数据时， 默认是按id将数据分发到不同的Node. 用户可以使用routing机制， 控制数据所在的分片。

routing 默认是使用`Murmur3HashFunction`, 可以通过`index.legacy.routing.hash.type`参数来控制。
比如:
```
index.legacy.routing.hash.type=org.elasticsearch.cluster.routing.SimpleHashFunction
```

为什么需要这样的参数呢？

通常我们使用routing机制时，很难保证数据均匀分布在所有的Node。当数据出现倾斜时，一方面会出现单个节点负载失衡；另一方面也影响系统性能。
通过这个参数的控制，能更精细地控制数据的分布。


