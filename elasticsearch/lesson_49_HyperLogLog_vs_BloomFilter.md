es的基数聚合使用到了hyperloglog算法。 出于好奇，了解了一下。

在海量数据场景下， 我们通常会遇到这样的两个问题:

1. 数据排重。比如在推送消息和发券场景下，不希望有重复。消息重复对用户是打扰， 重复发券就是损失了。

2. pv/uv统计。这类统计对精确度要求不是锱铢必较。 

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


