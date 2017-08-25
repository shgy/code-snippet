lesson_126遗留了3个问题。 
```
1. Client是如何启动RMAppMaster.
2. RMAppMaster是如何启动MapTask和ReduceTask.
3. MapTask和ReduceTask的协作方式。
```
其中， 问题1已经完结。 问题2和问题3还没有， 需要了解mapreduce的更多细节。
mapreduce的编程模型为用户开放了5个编程组件：`InputFormat, Mapper, Partitioner, Reducer, OutputFormat`。
从这里入手， 理解mapreduce的执行细节。
