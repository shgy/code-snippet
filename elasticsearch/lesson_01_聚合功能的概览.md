es的聚合有3种： 
1. Metric Aggregations  -- 指标聚合
2. Bucket Aggregations  -- 桶聚合
3. Pipeline Aggregations -- 管道聚合

Elasticsearch有一个功能叫做 聚合(aggregations) ，它允许你在数据上生成复杂的分析统计。它很像SQL中的 GROUP BY 但是功能更强大。

Aggregations种类分为:

Metrics, Metrics 是简单的对过滤出来的数据集进行avg,max等操作，是一个单一的数值。
Bucket, Bucket 你则可以理解为将过滤出来的数据集按条件分成多个小数据集，然后Metrics会分别作用在这些小数据集上。
对于最后聚合出来的结果，其实我们还希望能进一步做处理，所以有了Pipline Aggregations,其实就是组合一堆的Aggregations 
对已经聚合出来的结果再做处理。

聚合的性能优化方式：
0. 系统调优: 只使用half的系统内存(32G限制), 调整线程池, 调整缓存(fielddata), 调整refresh_interval 
1. 在查询中使用filter. 
2. 如果取topN, 可以改变聚合的集合模式： 将默认的深度优先变成广度优先， 及时裁剪， 避免OOM.
3. 多线程分别取, 然后手动合并。 比如全国32个地区， 启动32个线程， 同时取数据，以提高响应速度.
4. 使用常见的分表策略: 
      水平拆分(按日期, 按地域等将数据分到不同的表中), 
      垂直拆分(按业务模块, 将不同的字段放到不同的表中).
参考：
https://www.cnblogs.com/ghj1976/p/5311183.html
