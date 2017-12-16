
1. ClientRMService.forceKillApplication() 发送RMAppEventType.KILL事件

2. RMAppImpl.handle() 处理kill事件，KillAttemptTransition, 发送

3. RMAppImpl.handle() FinalSavingTransition 

4. RMStateStore发送RMStateStoreEventType.UPDATE_APP

2. RMAppAttemptImpl.handle(RMAppAttemptEvent.Kill)

3. RMStateStore.updateApplicationAttemptState 发送 RMStateStoreEventType.UPDATE_APP_ATTEMPT事件

4. ATTEMPT_UPDATE_SAVED
