前面已经粗略地了解了mapreduce开放的编程接口和mapreduce任务的执行流程。接下来在理解这些流程的基础上，
进一步地学习各个组件的工作原理及交互方式。

1. mapreduce对输入文件的处理。 

在wordcount中， 默认使用TextInputFormat类来处理输入。前面学习到， 自定义InputFormat，需要实现两个方法：
`getSplits()`和`createRecordReader()`

1.1 getSplits()
   
   split，即切分的意思。对于海量的数据，mapreduce的基本原则是分而治之。getSplits() 即用于实现数据的切分方案。 是的，getSplits()只是提供切分的方案。
如果说， 处理海量数据是一场战争，那么， getSplits() 承担的职责就是给出作战方案， 运筹帷幄之中。 决胜千里之外的任务自然是由`createRecordReader()`完成。
这个在下面会细说。 getSplits()输出的方案，是以InputSplits对象的方式给出。InputSplits包含如下的信息：
```java
public class FileSplit extends InputSplit implements Writable {
  private Path file;
  private long start;
  private long length;
  private String[] hosts;
  private SplitLocationInfo[] hostInfos;
  ...
}
```
要知道，HDFS作为分布式文件系统， 存储文件的有两个特点： 1. 支持超大文件（几百MB, 几百GB, 几百TB） 2. 文件按块分布在多个机器上(单个机器硬盘容量总有上限)。
分而治之 就是要像切蛋糕一样， 把整个文件化整为零， 让集群中每个机器可以同时处理属于它的那一部分。 只要机器的数量足够， 就能在很短的时间， 处理海量的数据。

这样， FileSplit的前3个字段就很好理解了： file定义需要处理的文件， start和length定义集群中某个机器处理的那一份。 至于hosts和hostInfos字段，先放一放。

接下来的问题就是： 如何确定FileSplit的start和length值呢？

这个涉及到Split的切分算法， 在《Hadoop 技术内幕》中有详细的描述。简单点理解就是尽量使每个split的大小跟hdfs的block size 一样。

getSplits()在哪里会被调用呢？在job提交的时候被调用。
以wordcount为例：
WordCount.main()
-->Job.waitForCompletion()
-->Job.submit()
-->JobSubmitter.submitJobInternal()
在提交之前， 会创建好splits， 并写入到文件中。
-->JobSubmitter.writeSplits()
-->JobSubmitter.writeNewSplits()

```
InputFormat<?, ?> input =
      ReflectionUtils.newInstance(job.getInputFormatClass(), conf);
      
List<InputSplit> splits = input.getSplits(job);
```
通过job配置好的InputFormat类， 用反射的方式实例化对象，然后调用`getSplits()`方法获取 Split的逻辑方案。
写入到`job.split`和`job.splitmetainfo`文件中。 


1.2 createRecordReader()
   
前面说到getSplits是运筹帷幄， createRecordReader是决胜千里。  createRecordReader() 在v1的版本中是getRecordReader()。
其功能为在MapTask中， 将InputSplit解析成一个个的Key/Value对， 供map接口使用。

例如， TextInputFormat默认的`LineRecordReader`，  在MapTask运行的时候被实例化。 

这里需要注意一个问题： 如果读取到InputSplit末尾， 但是行没有结束， 该怎么办？

前面说了， InputSplit只是方案， 既然是方案，那么在执行的时候可以根据实际情况，适当变通。不然如何决胜千里呢？
这里LineRecordReader的方式是：
```
除第一个InputSplit外， 每个InputSplit读取的时候都丢弃第一行的内容。
除最后一个InputSplit外， 每个InputSplit读取的时候都多读一行的内容。
```
这在前面已经反复提到。




前面了解到， 在job提交的时候， getSplits()的结果会写入到文件`job.split`和`job.splitmetainfo`中， 
那么这两个文件， 在那个阶段被读取呢？

以LocalJobRunner为例： 
在LocalJobRunner.Job.run()方法中， 读取文件`job.split`和`job.splitmetainfo`， 解析出Splits的个数等信息。
1. 根据Splits的个数， 创建MapTaskRunnable对象数组。
2. 在线程池中运行MapTaskRunnable线程对象。
---- MapTaskRunnable执行
3. 根据Splits信息创建Reader
4. 读取Splits的信息并使用用户编写的Mapper处理数据。

在YARNRunner中， 是如何执行的呢？

1. Client提交任务后， 启动了MRAppMaster
2. MRAppMaster调用start()方法启动后会创建JobImpl对象(见MRAppMaster.createJob())。
3. JobImpl初始化时(见JobImpl.InitTransition.transition()方法)会读取`job.split`和`job.splitmetainfo`， 
   解析出Splits的个数等信息。
4. 根据splits信息， 创建MapTaskImpl对象
5. 在Task开始调度(TaskImpl.InitialScheduleTransition.transition())时创建MapTaskAttemptImpl对象。
---> TaskImpl.InitialScheduleTransition.transition()
--->  TaskImpl.addAndScheduleAttempt()
--->  TaskImpl.addAttempt()
--->  MapTaskImpl.createAttempt()

6. 在Container被指派(TaskAttemptImpl.ContainerAssignedTransition.transition())时， 
   创建MapTask对象(MapTaskAttemptImpl.createRemoteTask())，
   等待Container启动后， 通过RPC远程获取。 
   
7. YarnChild启动后， 通过RPC获取MapTask (见TaskUmbilicalProtocol.getTask()方法)， 然后执行。
过程跟LocalJobRunner中的一样了
```
3. 根据Splits信息创建Reader
4. 读取Splits的信息并使用用户编写的Mapper处理数据。
```