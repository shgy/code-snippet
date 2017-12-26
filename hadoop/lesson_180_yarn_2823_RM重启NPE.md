http://hadoop.apache.org/docs/r2.6.0/hadoop-project-dist/hadoop-common/releasenotes.html
https://issues.apache.org/jira/browse/YARN-2823
```
The problem is on recovery, if the previous attempt already finished, we are not adding it the scheduler.
when scheduler tries to transferStateFromPreviousAttempt for work-presrving AM restart, it throws NPE.
```

这个问题是由RM的重启恢复特性带来的. 需要了解一下RM重启的过程. 这个bug跟2834是类似的. 再回顾一下RM的恢复过程.


---- 搞懂了---
关键代码在: 
```
// Bug修复代码
appAttempt.scheduler.handle(new AppAttemptAddedSchedulerEvent(
appAttempt.getAppAttemptId(), false, true));

(new BaseFinalTransition(appAttempt.recoveredFinalState)).transition(
  appAttempt, event);
```

1. 如何复现Bug?
  先看patch, https://issues.apache.org/jira/secure/attachment/12679967/YARN-2823.1.patch
  由于Bug的patch只有2行代码(TestCase不算), 因此在2.6.0的源码中注释这两行代码, 然后运行`testRMRestartAppRunningAMFailed()`方法.
  即可看到如下的log:
  ``` 
  2017-12-26 18:14:23,572 FATAL [ResourceManager Event Processor] resourcemanager.ResourceManager (ResourceManager.java:run(689)) - Error in handling event type APP_ATTEMPT_ADDED to the scheduler
java.lang.NullPointerException
	at org.apache.hadoop.yarn.server.resourcemanager.scheduler.SchedulerApplicationAttempt.transferStateFromPreviousAttempt(SchedulerApplicationAttempt.java:567)
	at org.apache.hadoop.yarn.server.resourcemanager.scheduler.common.fica.FiCaSchedulerApp.transferStateFromPreviousAttempt(FiCaSchedulerApp.java:307)
	at org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler.addApplicationAttempt(CapacityScheduler.java:740)
	at org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler.handle(CapacityScheduler.java:1089)
	at org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler.handle(CapacityScheduler.java:114)
	at org.apache.hadoop.yarn.server.resourcemanager.ResourceManager$SchedulerEventDispatcher$EventProcessor.run(ResourceManager.java:680)
	at java.lang.Thread.run(Thread.java:745)
  ```
除了行号跟bug-report `https://issues.apache.org/jira/browse/YARN-2823`不一样,其他的都一样.

2. Bug出现的原因是啥?
 
 这个在Bug的Comments中已经说明了.
```
The problem is on recovery, if the previous attempt already finished, we are not adding it the scheduler.
when scheduler tries to transferStateFromPreviousAttempt for work-presrving AM restart, it throws NPE.
```

即已经完成的attempt不会添加到scheduler中, 当scheduler试图以`transferStateFromPreviousAttempt`的方式启动attempt时, 
由于找不到历史的attempt, 所以就抛出了NPE异常.

补充一点: 
   TestCase的构造即依据上面的缘由分析: App中的attempt处于终结状态(运行失败), 重启RM后, 为了app能正常运行, 需要构造出attempt.
为了能尽可能节约集群的资源, 我们需要"断点续传", 即从历史的attempt状态开始, 而非从0开始.
   2.6.0版本前的代码: 如果检测到attempt处于终结状态, 则不添加到调度器中. 所以后面启动的attempt希望用到历史attempt的状态时,就
找不到了, 在代码上的表现既是: NPE.
   
再补充一点: scheduler作为一个相对独立的组件,在其内部维护了自己的一套: 
app    (SchedulerApplication)
attempt(SchedulerApplicationAttempt)
node   (SchedulerNode)
这些信息.

3. 理解Bug的关键路径: RM的恢复过程
ResourceManager.RMActiveServices.serviceStart()
----> ResourceManager.recover()
------->RMAppManager.recovery()
1. 依次恢复在集群上运行的应用.
2. 根据RMStore存储的信息创建RMAppImpl状态机.
3. 通知RMAppImpl处理RMAppEventType.RECOVER事件.
4. 接收到事件后, RMAppImpl创建所有的RMAppAttempImpl状态机, 参见`RMAppRecoveredTransition.transition() --> app.recover()`
5. 然后将RMAppAttemptImpl状态机的状态恢复到RM重启前的状态. 参见`RMAppImpl.recover() --> attempt.recover() `
6. 通知RMAppAttemptImpl处理`RMAppAttemptEventType.RECOVER`事件
7. 将失败的attempt添加到scheduler中 (BUG修复的代码)
8. 调用BaseFinalTransition().transtion()进入最终状态.
9. 通知RMAppImpl处理RMAppEventType.ATTEMPT_FAILED事件.
10. 创建新的attempt, 重新运行app
``` 
RMAppAttempt oldAttempt = app.currentAttempt;
app.createAndStartNewAttempt(transferStateFromPreviousAttempt);
```