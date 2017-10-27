前面都已经介绍过MRAppMaster，为什么这里又开始了解MRAppMaster呢？

窃以为， 学习MapReduce框架，分为两部分： 
1. LocalJobRunner为核心MapReduce的数据处理流程。 通过LocalJobRunner， 可以了解到数据在整个RM过程中的流向。
   Map:    源文件--> InputSplit -> map() --> 环形缓冲区 --> Merge --> file.out 
   Reduce: shuffer --> Merge --> Sort -- reduce()  ---> part-xxx.out

2. MRAppMaster为核心的资源调度， 作业管理流程。 通过MRAppMaster, 可以了解到集群的资源是如何为Job服务的。

前面学习MRAppMaster, 没有深入， 主要是没有学习Yarn的工作原理。 正好前段时间初步学习了一遍Yarn， 正好可以
通过MRAppMaster来巩固Yarn的基础知识。

学习yarn-NM的时候， 发现状态机其实没有那么难理解。 重点是要把握主线。

在RMAppMaster上， 有Job, Task, TaskAttempt 这3个状态机。

Job 状态机的主线是： NEW --> INITED --> SETUP --> RUNNING --> COMMITTING -->  SUCCEEDED
