mapreduce中， 用户自定义的maptask的的输出结果会调用MapTask.collect()方法，
这就是缓冲区的入口。整个缓冲区相关的类是MapTask.MapOutputBuffer.

这里定义的环形缓冲区感觉有些难度，而且hadoop2 跟hadoop又有些不一样。

mapreduce作为一个框架， 对用户来说就是一个黑盒。但是如果想对mapreduce进行调优， 是有如下的基本法则的:
1. 你无法正确地调整你不理解的东西。
2. 你无法提升你不能度量的东西。


环形缓冲区的资料， MAPREDUCE-64的图不错。

缓冲区是定义在一个数组中， 这个数组记录了两部分数据： kvmeta和kvbuffer. 这种组合也算是常见的， 目录+正文。


一. 环形缓冲区的初始化：

1. 由于一条元数据(kvmeta)占用16byte, 所以 buf数组是16的整数倍。
2. 设置最多可用缓冲区的80%，  meta + data 一共。
3. 在缓冲区没有填满的情况下， 每次写入， 变化的只有 kvindex （--） 和 bufindex ( ++ ) 


``` 
bufstart = bufend = bufindex = equator;   从0开始
kvstart  = kvend  = kvindex;    从21264396开始
```

3. 环形缓冲区的每次写入的是两种信息： meta 和 kv, 而且 meta是从右往左， kv是从左往右。 

4. 每次spill的区间都是： [bufstart, bufend] 和 [kvstart, kvend] 这端区间。 所以， 
`bufstart`和`bufend` 及 `kvstart`和`kvend`是用于 spill 线程的。


几个重要参数： 
```
JobContext.IO_SORT_MB   mapreduce.task.io.sort.mb  = 100 
```
这个参数决定了缓冲区的大小， 默认是100M, 

```
JobContext.MAP_SORT_SPILL_PERCENT = "mapreduce.map.sort.spill.percent" = 0.8
```
这个参数决定了spill的触发条件，默认是缓冲区用到80%写入到文件中。 但实际上， 由于缓冲区不仅存kv， 也存meta.
所以， spill.out的大小可能只有100*0.6 = 60M。 
例如， 我设置`mapreduce.task.io.sort.mb`=1M，  所有的数据只有`hello world hel`这3个单词，结果每个spill.out只有371K.


总结： 
环形缓冲区承担的任务是 将MapTask的结果分批写入到 spill.out 文件中. 相当于在在IO操作中间加了一趟班车， 等人满了再发车。
这一阶段， 了解其用途及基本原理即可， 先回避这一块复杂的算法规则。 后续， 如果有需要， 再深究。






参考：
http://www.itwendao.com/article/detail/382775.html
http://bigdatadecode.club/MapReduce%E6%BA%90%E7%A0%81%E8%A7%A3%E6%9E%90--%E7%8E%AF%E5%BD%A2%E7%BC%93%E5%86%B2%E5%8C%BA.html
https://www.slideshare.net/cloudera/mr-perf/10-Mapside_sortspill_overview_Goal_when
https://issues.apache.org/jira/browse/MAPREDUCE-64
