https://issues.apache.org/jira/browse/MAPREDUCE-6921

The error is introduced post the HADOOP-14289 commit due to migration from log4j APIs to slf4j.

hadoop 的log组件从 log4j 移植到slf4j 带来的问题。

为啥要使用slf4j, 而不是log4j? 

1. slf4j是一个抽象层， 而非具体的实现层。 使用它可以方便用户切换日志实现方案。

2. 支持模板， 避免了 debug, info, 这类日志在生产环境 由字符串的相加 带来的性能损耗。


参考： 
http://www.importnew.com/7450.html
http://www.cnblogs.com/hehaiyang/p/4261816.html