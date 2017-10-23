MRAppMaster启动后， 需要周期性的向RM报告自己活着的信息。在RM端， 由AMLivelinessMonitor来负责处理上报的信息。
关于AMLivelinessMonitor, 参考`lesson_95_AMLivelinessMonitor.md`.
在RMAppMaster端， 由谁负责呢？ 首先，不管谁负责， 都需要遵循 ApplicationMasterProtocol 协议。 
``` 
registerApplicationMaster
finishApplicationMaster
allocate
```

`allocate` 由`RMContainerAllocator`周期性调用(参考RMCommunicator.tartAllocatorThread()方法)。
默认每秒1次
```
/** How often the AM should send heartbeats to the RM.*/
public static final String MR_AM_TO_RM_HEARTBEAT_INTERVAL_MS =
MR_AM_PREFIX + "scheduler.heartbeat.interval-ms";
public static final int DEFAULT_MR_AM_TO_RM_HEARTBEAT_INTERVAL_MS = 1000;

```

由于ResourceManage为ApplicationMaster申请资源的过程涉及到多个状态机的转换， 比较复杂，所以先放一放。