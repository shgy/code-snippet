AMLivelinessMonitor用于监控ApplicationMaster是否活着。 其工作原理为：

维护一个名为running的map<ApplicationAttemptId, Long>, 该map记录着ApplicationAttemptId最后的心跳上报时间。
PingChecker的线程周期性(默认100s)的遍历map, 用当前时间与ApplicationAttempId最后的上报时间对比。 如果时间差值大于设定的间隔(默认5分钟)
则认为过期， 并从队列中删除该ApplicationAttemptId, 然后发送RMAppAttemptEventType.EXPIRE事件到异步分发队列。由相关的组件处理过期的后果。

这里一般就会有两个疑问：
1. running队列在哪里更新的？
2. ApplicationMaster过期，系统是怎样处理的？


问题1： running队列在哪里更新的？
我们关注3个方法：
```AbstractLivelinessMonitor.class

  public synchronized void receivedPing(O ob) {
    //only put for the registered objects
    if (running.containsKey(ob)) {
      running.put(ob, clock.getTime());
    }
  }

  public synchronized void register(O ob) {
    running.put(ob, clock.getTime());
  }

  public synchronized void unregister(O ob) {
    running.remove(ob);
  }
```

对于receivedPing(), 一共有5处调用：
1. ApplicationMasterService.allocate()                   -- 请求资源
2. ApplicationMasterService.finishApplicationMaster()    -- 任务结束
3. ApplicationMasterService.registerApplicationMaster()  -- 任务注册
4. ResourceTrackerService.nodeHeartbeat()                -- 心跳
5. RMAppAttemptImpl.StatusUpdateTransition.transition()  -- 状态的转移

问题2： ApplicationMaster过期，系统是怎样处理的？

解决这个问题， 要先理解RMAppAttemp的状态机


最后， 与AMLivelinessMonitor结构类似的还有 NMLivelinessMonitor, ContainerAllocationExpirer.
接下来就抛出新的问题：
receivedPing() 一共有5处调用， 5出调用是怎么分配到这3个功能模块上来的呢？

AMLivelinessMonitor: 1,2,3,5
NMLivelinessMonitor: 4
ContainerAllocationExpirer: