https://issues.apache.org/jira/browse/MAPREDUCE-6889

这个bug涉及到了Yarn。 
```
 FileSystemTimelineWriter which creates FS object on every writer initialization. 
 If writer is not closed,then there is possibility of OOM see YARN-5438 fixes closing FS object.
```
要先了解一下 `FileSystemTimelineWriter` 是个啥东西。

1. FileSystemTimelineWriter 位于 hadoop-yarn-common包中, 看来是比较基础和底层的了。

2. TimelineClientImpl类用到了`FileSystemTimelineWriter`。

3. The YARN Timeline Server 解决了什么样的需求问题？

 通常， application运行在yarn上时， 我们希望对它进行监控，能够看到该应用相关的信息。比如： 
 1. application当前的状态
 2. application使用的container的状态
 ...
 
 这些数据要写到HDFS上。 如果每个FileSystemTimelineWriter创建的FS对象不close(), 则有内存泄露的风险。
 
 

