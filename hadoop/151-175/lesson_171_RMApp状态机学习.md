1. 客户端提交启动App请求后, RMAppManager就会创建RMApp状态机， 此时， RMAppImpl状态机进入NEW状态。

RMAppImpl的主线如下：
NEW --> NEW_SAVING --> SUBMITTED --> ACCEPTED --> RUNNING -->  FINAL_SAVING --> FINISHING --> FINISHED
     1              2             3            4           5                 6             7

----------------------------------------------
  
1: --- RMAppEventType.START                事件
2: --- RMAppEventType.APP_NEW_SAVED        事件
3: --- RMAppEventType.APP_ACCEPTED         事件
4: --- RMAppEventType.ATTEMPT_REGISTERED   事件
5: --- RMAppEventType.ATTEMPT_UNREGISTERED 事件
6: --- RMAppEventType.APP_UPDATE_SAVED     事件
7: --- RMAppEventType.ATTEMPT_FINISHED     事件



RMAppImpl接收到 事件3(`RMAppEventType.APP_ACCEPTED`), 后会创建 `RMAppAttempt`状态机。
RMAppAttempt的主线如下： 
NEW --> SUBMITTED --> SCHEDULED --> ALLOCATED_SAVING --> ALLOCATED --> LAUNCHED --> RUNNING --> FINAL_SAVING --> FINISHING --> FINISHED
     1             2             3                    4             5            6           7                8             9
     
1: RMAppAttemptEventType.START
2: RMAppAttemptEventType.ATTEMPT_ADDED
3: RMAppAttemptEventType.CONTAINER_ALLOCATED
4: RMAppAttemptEventType.ATTEMPT_NEW_SAVED
5: RMAppAttemptEventType.LAUNCHED
6: RMAppAttemptEventType.REGISTERED
7: RMAppAttemptEventType.UNREGISTERED
8: RMAppAttemptEventType.ATTEMPT_UPDATE_SAVED
9: RMAppAttemptEventType.CONTAINER_FINISHED