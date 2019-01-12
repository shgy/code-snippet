我们需要查看数据的统计量时，均值是最重要的特征之一。

对于海量数据，这类简单的聚合ES可以做到秒级别返回。聚合是ES的特色功能。

那么ES是如何实现这一功能的呢？ 

我们知道，ES的数据存储在各个节点中， 所以ES的实现AvgAggregation时基本思路就是先统计各个节点，然后汇总。


先了解ES是如何统计单个节点: 参考AvgAggregator
```
    @Override
    public LeafBucketCollector getLeafCollector(LeafReaderContext ctx,
            final LeafBucketCollector sub) throws IOException {
        if (valuesSource == null) {
            return LeafBucketCollector.NO_OP_COLLECTOR;
        }
        final BigArrays bigArrays = context.bigArrays();
        final SortedNumericDoubleValues values = valuesSource.doubleValues(ctx);
        return new LeafBucketCollectorBase(sub, values) {
            @Override
            public void collect(int doc, long bucket) throws IOException {
                counts = bigArrays.grow(counts, bucket + 1);
                sums = bigArrays.grow(sums, bucket + 1);

                values.setDocument(doc);
                final int valueCount = values.count();
                counts.increment(bucket, valueCount);
                double sum = 0;
                for (int i = 0; i < valueCount; i++) {
                    sum += values.valueAt(i);
                }
                sums.increment(bucket, sum);
            }
        };
    }
```

即实现Collector类的collect()方法。然后通过`doc_values`机制获取文档相关字段的值，分别汇入counts和sums两个变量中。

收集完成counts和sums过后，就需要汇总各个节点的值, 这在搜索的第二阶段。 整个链路如下: 
s1: 前端请求发送到集群某一节点的`TransportSearchAction.doExecute()`方法中。
```
     switch(searchRequest.searchType()) {
               .....
           case QUERY_THEN_FETCH:
                searchAsyncAction = new SearchQueryThenFetchAsyncAction(logger, searchService, clusterService,
                        indexNameExpressionResolver, searchPhaseController, threadPool, searchRequest, listener);
                break;
              ......   
     }
        searchAsyncAction.start();
```
见到start()方法，我以为这个是另启一个线程，后面发现原来不是的。 这个start()方法把整个查询过程分为两个阶段:

阶段一： 
  performFirstPhase(), 即把请求分发到各个节点，然后记录节点处理的结果。如果返回的分片是最后一个分片，则转入阶段二。

阶段二： 
  performFirstPhase() -> onFirstPhaseResult() -> innerMoveToSecondPhase() -> moveToSecondPhase() 。这里利用了模板设计模式。在阶段二中，会再次向各个节点发起请求，通过docId获取文档内容。

s2: 对于聚合而言， 阶段二最重要的链路是moveToSecondPhase() -> executeFetch() ->  finishHim() -> searchPhaseController.merge() , merge()中包含了如下的业务逻辑: 合并hits, 合并suggest， 合并addAggregation 等。 这里我们关注聚合。

聚合的入口方法是`InternalAggregations.reduce()`, 如果熟悉hadoop, reduce方法的执行逻辑看这个名字也能理解一部分。reduce的中文翻译“归纳”，挺生动形象的。整个链路的入口为`InternalAvg.doReduce()`。
```
    @Override
    public InternalAvg doReduce(List<InternalAggregation> aggregations, ReduceContext reduceContext) {
        long count = 0;
        double sum = 0;
        for (InternalAggregation aggregation : aggregations) {
            count += ((InternalAvg) aggregation).count;
            sum += ((InternalAvg) aggregation).sum;
        }
        return new InternalAvg(getName(), sum, count, valueFormatter, pipelineAggregators(), getMetaData());
    }
```
其逻辑相当简单，count相加， sum相加。获取最终的结果就是
```
    public double getValue() {
        return sum / count;
    }
```

上面讲述了ES分发会汇总的关键节点，那么分发到各个节点的业务逻辑是怎样的呢？

首先定位入口:
```
    class SearchQueryTransportHandler extends TransportRequestHandler<ShardSearchTransportRequest> {
        @Override
        public void messageReceived(ShardSearchTransportRequest request, TransportChannel channel) throws Exception {
            QuerySearchResultProvider result = searchService.executeQueryPhase(request);
            channel.sendResponse(result);
        }
    }
``
然后定位到`QueryPhrase.execute()`, 在QueryPhrase这个阶段，主要做的事情如下:

` aggregationPhase.preProcess(searchContext)`: 解析ES的语法，生成Collector.
`execute`: 在调用Lucene的接口查询数据前，组合各个Collecotr， ` collector = MultiCollector.wrap(subCollectors);` 然后查询Lucene索引。对于AvgAggregator, 其关键逻辑是:
```
            @Override
            public void collect(int doc, long bucket) throws IOException {
                counts = bigArrays.grow(counts, bucket + 1);
                sums = bigArrays.grow(sums, bucket + 1);

                values.setDocument(doc);
                final int valueCount = values.count();
                counts.increment(bucket, valueCount);
                double sum = 0;
                for (int i = 0; i < valueCount; i++) {
                    sum += values.valueAt(i);
                }
                sums.increment(bucket, sum);
            }
```
这个已经是第二次出现了， 它的功能就是收集每个命中查询的doc相关信息。 这里获取每个docId对应的value，是基于doc_value的正向索引。


以上就是整个Avg Aggregation的实现流程。 通过源码，可以确认， AvgAggregation是精确可信的。 还有几个聚合函数，其思路跟AvgAggregation是一致的，就不细说了，他们分别是： Max, Min, Sum, ValueCount, Stats 。。。













