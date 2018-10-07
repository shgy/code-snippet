hadoop生态是一个很好的数据存储，处理的平台。但是对于高并发的业务需求，即使有hbase这样的NoSQL, 也很难满足业务需求。

1. hadoop比较重量级， 各个组件是耦合的，如果出问题，定位和解决是比较耗时的。

2. hadoop平台基于JVM, Java在数据库层面相比c/c++而言，目前还是处于劣势的。

通常有些业务需要将数据从hive导出到redis中，供系统使用。 这就需要有工具能够支持将数据从hive导出到redis。 受elasticsearch-hadoop的思路启发，
希望能够制作一个比较通用的工具，支持从hive导出数据到redis。

s1: 编译elasticsearch-hadoop源码。 借鉴源码是快速开发的捷径。
```

```

s2: 学习EsStorageHandler的实现


s3: 基于EsStorageHandler的源码构造出测试环境。

   测试环境至关重要， 有了强大的测试环境，就能快速开发试错了。 不然每次代码开发完了，到集群上通过日志的方式定位问题，修复重试，整个周期太长了。

s4: 开发测试


s5: 集群环境测试


