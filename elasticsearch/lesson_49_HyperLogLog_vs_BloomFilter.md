
ES的聚合是其一大特色。然而出于性能的考虑， ES的聚合是以分片Shard为单位，而非Index为单位， 所以
有些聚合的准确性是需要注意的。 比如： TermAggregations.


es的基数聚合使用到了hyperloglog算法。 出于好奇，了解了一下。

在海量数据场景下， 我们通常会遇到这样的两个问题:

1. 数据排重。比如在推送消息场景，消息重复对用户是打扰， 用户发券场景， 重复发券就是损失了。

2. pv/uv统计。这类场景下， 对精确度要求没必要锱铢必较。 

如何高效解决这两类问题呢？

对于数据排重， 我们可以使用布隆过滤器。java 样列代码如下: 
```
BloomFilter<String> bloomFilter = BloomFilter.create(new Funnel<String>() {

            private static final long serialVersionUID = 1L;

            @Override
            public void funnel(String arg0, PrimitiveSink arg1) {

                arg1.putString(arg0, Charsets.UTF_8);
            }

        }, 1024*1024*32);

        bloomFilter.put("asdf");
        bloomFilter.mightContain("asdf");
```

对于计数， 我们可以使用HyperLogLog算法，ES中已经有相关的实现。

其实封装一下，布隆过滤器也是能直接实现HyperLogLog算法的功能的。 

这里遗留几个问题，思考清楚后补充:
1. BloomFilter跟HyperLogLog算法的原理
2. 相同量级数据下的效率及内存消耗
3. 各自的适用场景有哪些


