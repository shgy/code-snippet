elasticsearch的getting started文档描述的其典型的4种应用场景：
1. 产品搜索
这个是其最核心和本真的功能： index + query
2. 日志分析
这个是其独有的功能特色： ELK 
3. 消息通知
这个利用其反向查询的功能。 即通过数据匹配Query，从而得到预警的效果。 比如商品降价的通知，关键词过滤等应用。 Percolator
4. BI平台
这个其实就是报表分析： aggregations

通常1,2,4是应用最多的。 

由于垂直电商的兴起，lucene/solr迎来了他们的春天，因为lucene/solr满足了快速搭建搜索引擎的需求。由于垂直电商SKU数量不多，因此， 搜索的难点在业务逻辑而不在数据量。由于大数据的兴起， solr在满足海量数据的存储和搜索需求上有些力不从心，elasticsearch脱颖而出。

我试图按照对es的需求是否核心进行分类：
核心层： 垂直行业的搜索部门， 手机的app store。 比如电商/物流平台。 其中app store应该重点突出一下，因为竞价排名。
中坚层： 互联网公司大数据业务。 比如数据平台，BI系统啥的。 这个一般是结合其他系统使用， 对ES功能的挖掘并不强烈。
支撑层： 运维部门。 ELK不解释。

不同的层次对es的依赖程度是不一样的， 有的是没它不行， 有的是可有可无。 这中间的区别对技术成长差异还是挺大的。

个人觉得 es 跟 hadoop 平台是强关联的， 虽然从集群的角度来说， 他们是独立的。

接下来， 会大致浏览一下ES功能的变迁，了解各个历史版本提供的特性。

```
v2.0
1. rivers功能被移除。
  river的出发点很简单： 通过插件的方式自动索引数据到es集群。 但是支持插件这个做法， 本身就有一个难以回避的问题，那就是系统的稳定性。
因为插件代码的质量是没法控制的。而且river这个插件应用场景又相当广泛，一旦出问题， 责任全在es上了。 所以到了v5.0 索性连插件都不支持了。

2. facets功能被移除
   这个移除没啥说的， 因为有aggregations这个更强大的功能存在。

3. mvel 脚本语言支持移除了， 只支持groovy
   宁缺毋滥

4. delete-by-query变成了插件
   delete-by-query 这个删数据太方便了， 这么方便的功能就是个定时炸弹。 一旦误操作，只能从删库到跑路了。而且es有alias功能，删数据有更优雅的做法

5. multicast Discovery 变成了插件
   这个是系统强依赖的功能， 如果集成到core中，只能不停地打patch.  所以es的zen就只支持unicast了， 这就需要在配置集群的时候添加`discover.zen.ping.unicast.hosts`参数，这个不影响es的使用。

6. `_shutdown` API被移除， 没有替代品
  这个也是防止误操作了， 自由还是有个度的。

7. `murmur3` 变成插件
  aggregations 性能优化， 非必须。 

8. `_size` 变成插件
  这个没用过， 不予置评。

9. thrift和memcached 通信方式移除。
  只能通过http 和 JavaAPI

10. Bulk UDP API移除
  
  有标准的bulk API


11. MergeScheduler 不再是插件了，应该是集成到core中了。

```
一些重大的change
```
1. Multiple path.data striping

v2.0之前 如果path.data设置成多个目录(多块盘场景)， 一个分片会被拆分到多个目录中， 以实现充分利用多块盘的目的。
但是这样做有个问题： 如果一块盘坏了， 那么该节点所有的分片数据都会损失，这个节点就没法用了。 一般配置8块盘的
机器， 损失了一块盘，还有7块盘可用。 从v2.0开始， 同一个分片上的文件都会集中到同一个目录。 
对于充分利用磁盘IO的问题， 同一个节点上多几个副本就OK了。

2. 动态添加的field, 其mapping必须得到master node 的确认，然后才能索引数据。
   
   如果没有这个约束， 有可能会导致同一个field出现不同的mapping, 轻则查询结果出错， 重则索引文件崩溃。
   有这个约束， 会导致数据索引的速度变慢(所以尽量预先配置好mapping显得更重要)。

3. 同一个index不同type中field的mapping类型必须相同。 这个有点坑， 就是说field_type是index作用域，而不是type作用域。
   这个应该更es组织数据的方式有关， shard是index作用域的。 这个后面深入了解原因。

4. Type meta-fields
   这里重点看`_routing`参数。 v2.0以前， es是依据文档内部的一个字段来指定routing, 现在变成了显式的参数
   这个变化让人在处理的时候更清晰自己在干啥，以免出现问题，忘记了_routing的存在。 关于routing值得单独一个系列来写。
   因为这是es的分布式特性依赖的基石。
 

  

```




参考：
https://www.elastic.co/blog/deprecating-rivers
