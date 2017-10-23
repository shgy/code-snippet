APP_ADDED事件处理的过程中会给调度器发送事件APP_ATTEMPT_ADDED.

这个事件， 调度器是如何处理的呢？ 

1. 创建`FiCaSchedulerApp`对象。 

2. 如果需要复制上次Attempt的状态， 则复制 `transferStateFromPreviousAttempt`。

3. 发送 RMAppAttemptEventType.ATTEMPT_ADDED 事件 给 RMAppAttemptImpl对象。

4. RMAppAttemptImpl.ScheduleTransition() 

5. 这里有个分支： UnmanangedAM 和 managedAM.

6. 如果是 `managedAM`， 则调用`appAttempt.scheduler.allocate()`方法。

7. 如果是`UnmanagedAM`, 则执行 `appAttempt.storeAttempt();` 代码。

注： 暂时不考虑UnmanagerAM 这一块的东西，以免增加复杂度。

隐隐感觉Scheduler 的调度 到AppMaster这一层就结束了。

8. `appAttempt.scheduler.allocate()` 感觉就是做了一个登记处理。
所以， 实际调度还是在`NODE_UPDATED`中。


