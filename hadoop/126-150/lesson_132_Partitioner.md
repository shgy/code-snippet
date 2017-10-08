Partitioner位于Mapper和Reducer的中间。 其作用为将Mapper的输出进行分片， 方便Reducer处理。
默认是Hash分片。 这也是发生数据倾斜的源头。 比如Hbase, 如果rowkey设计不好， 很容易就数据倾斜了。



http://www.cnblogs.com/esingchan/p/3947156.html
