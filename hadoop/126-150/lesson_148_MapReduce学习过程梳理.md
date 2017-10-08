学到这里， 有必要梳理一遍学习mapreduce的过程。

1. 由于开始学习过Yarn， 所以MapReduce的切入点是Yarn. 把MapReduce看成是Yarn的一个应用。认为MapReduce需要遵循Yarn的接口。
从而寻找`Yarn-App需要用到的3个RPC协议` 在MapReduce框架中是怎样实现的.

2. 由于Yarn的根基很浅， 感觉困惑很多， 所以转换思路。从MapReduce开放的编程接口作为切入点。这样做是对的。

3. 学习了几个接口`InputFormat`和`OutputFormat`等几个接口后， 回到了Yarn， 尝试回答最初的几个问题。这样做是不对的。原因在后面解释。

4. 感觉对mapreduce的执行过程仍有疑惑， 再次关注InputFormat/OutputFormat, 找到了LocalJobRunner

5. 基于LocalJobRunner和WordCount来了解MapReduce的整个过程。

6. 通过jira来验证自己对mapreduce的理解是否比较全面。


虽然mapreduce是基于yarn的应用。 但是mapreduce的核心不在于yarn， 就像spark/hbase/storm都是基于yarn的应用一样。
所以，从yarn作为切入点 其实就是切偏了。 正确的切入点是 `wordcount + LocalJobRunner`.

1. wordcount足够简单
2. 程序运行不需要集群， 依赖环境简单， 可以单步调试。 

对于mapreduce而言， yarn不是必须的。

接下来， 借助MRAppMaster/distributedshell 来了解Yarn的调度器。




