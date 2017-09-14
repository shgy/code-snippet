
学Yarn的过程中，接触到了Yarn应用的状态机。 mapreduce中也应用到了状态机。 使用如下的命令编译mapreduce源码。
`mvn compile -Pvisualize`
然后使用命令
`dot -Tpng MapReduce.gv > MapReduce.png`
即可看到MapReduce的状态机。

一共有Job, Task, TaskAttempt 3个状态机。

1个Job有一个或者多个Task, 一个Task在集群运行的时候可能会尝试多次(TaskAttempt)


前面遗留了两个问题：
2. RMAppMaster是如何启动MapTask和ReduceTask？
3. MapTask和ReduceTask的协作方式？


RMAppMaster启动MapTask和ReduceTask任务的个数由数据量的大小决定， 即InputSplit.


MRAppMaster.main()
--> MRAppMaster.initAndStartAppMaster()
--> MRAppMaster.serviceStart()

step 1: 
----> MRAppMaster.createJob()   -- 创建Job

setp 2: 初始化Job, 这里没有采用异步处理的方式
----> MRAppMaster.jobEventDispatcher.handle(initJobEvent) 
----> JobImpl.InitTransition.transition()

1. 确定maptask的个数， 由InputSplit决定。
2. 判定job的运行模式uber或者non-uber.
```
基于作业大小因素，MRAppMaster提供了三种作业运行方式：本地Local模式、Uber模式、Non-Uber模式。其中，
1、本地Local模式：通常用于调试；
2、Uber模式：为降低小作业延迟而设计的一种模式，所有任务，不管是Map Task，还是Reduce Task，
均在同一个Container中顺序执行，这个Container其实也是MRAppMaster所在Container；
3、Non-Uber模式：对于运行时间较长的大作业，先为Map Task申请资源，当Map Task运行完成数目达到一定
比例后再为Reduce Task申请资源。
```
3. 创建MapTask和ReduceTask 

到这里， init即初始化的工作完成。

step 3: 启动任务
----> MRAppMaster.startJobs()
----> JobImpl.StartTransition.transition()  -- 异步，不像init时是同步的操作。
----> CommitterEventHandler.EventProcessor.run()
---->JobImpl.SetupCompletedTransition.transition()
---->TaskImpl.InitialScheduleTransition.transition()
----> TaskAttempImpl.RequestContainerTransition.transition()
----> RMContainerAllocator.handleEvent()
  在RMContainerAllocator中等待心跳， 发送TaskAttemptContainerAssignedEvent事件。
----> ContainerLauncherImpl.EventProcessor.run()
----> Container.launch()

 调度作业的Map Task, 调度作业的Reduce Task；


mapreduce有3个状态机： JobImpl, TaskImpl, TaskAttempImpl

下面是各种类型的状态机处理器

dispatcher.register(org.apache.hadoop.mapreduce.jobhistory.EventType.class, historyService);
dispatcher.register(JobEventType.class, jobEventDispatcher);
dispatcher.register(TaskEventType.class, new TaskEventDispatcher());
dispatcher.register(TaskAttemptEventType.class, new TaskAttemptEventDispatcher());
dispatcher.register(CommitterEventType.class, committerEventHandler);

dispatcher.register(Speculator.EventType.class,speculatorEventDispatcher);

dispatcher.register(ContainerAllocator.EventType.class, containerAllocator);

dispatcher.register(ContainerLauncher.EventType.class, containerLauncher);


ContainerLauncherImpl 最终启动container的代码

研读MapReduce的代码， 很容易被状态机绕晕。

参考：http://blog.csdn.net/lipeng_bigdata/article/details/51288673