简单了解了一下App的调度过程， 既然调度器是一个plugin, 那么，调度器应该相当独立。
因此了解一下APP_ADDED事件的处理过程：

1. 添加app到 applications 容器中。
``` 
SchedulerApplication<FiCaSchedulerApp> application =
    new SchedulerApplication<FiCaSchedulerApp>(DEFAULT_QUEUE, user);
applications.put(applicationId, application);
```

2. 发送RMAppEventType.APP_ACCEPTED事件到RMAppImpl对象。

3. RMAppImpl.StartAppAttemptTransition()

4. 创建新的RMAppAttempt对象，并添加到attempts中管理起来。

5. 发送RMAppAttemptEventType.START事件到RMAppAttemptImpl对象。

6. RMAppAttempt.AttemptStartedTransition()

7. 向`ApplicationMasterService`中注册AppAttempt. 
```
appAttempt.masterService.registerAppAttempt(appAttempt.applicationAttemptId)
```

8. 发送SchedulerEventType.APP_ATTEMPT_ADDED事件给FifoYarnScheduler.


