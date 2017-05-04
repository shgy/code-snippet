```
[
  Service org.apache.hadoop.yarn.server.resourcemanager.RMSecretManagerService                   in state : STARTED
, Service org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.ContainerAllocationExpirer   in state : STARTED
, Service AMLivelinessMonitor                                                                    in state : STARTED
, Service AMLivelinessMonitor                                                                    in state : STARTED
, Service org.apache.hadoop.yarn.nodelabels.CommonNodeLabelsManager                              in state : STARTED
, Service org.apache.hadoop.yarn.server.resourcemanager.ahs.RMApplicationHistoryWriter           in state : STARTED
, Service org.apache.hadoop.yarn.server.resourcemanager.metrics.SystemMetricsPublisher           in state : STARTED
, Service org.apache.hadoop.yarn.server.resourcemanager.NodesListManager                         in state : STARTED
, Service org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler     in state : STARTED
, Service org.apache.hadoop.yarn.server.resourcemanager.ResourceManager$SchedulerEventDispatcher in state : STARTED
, Service NMLivelinessMonitor                                                                    in state : STARTED
, Service org.apache.hadoop.yarn.server.resourcemanager.ResourceTrackerService                   in state : STARTED
, Service SchedulingMonitor (ProportionalCapacityPreemptionPolicy)                               in state : STARTED
, Service org.apache.hadoop.yarn.server.resourcemanager.ApplicationMasterService                 in state : STARTED
, Service org.apache.hadoop.yarn.server.resourcemanager.ClientRMService                          in state : INITED
, Service org.apache.hadoop.yarn.server.resourcemanager.amlauncher.ApplicationMasterLauncher     in state : INITED
]
```
一共16个Service, 先分别了解一下. 不求理解清楚, 混个脸熟.
RMSecretManagerService --- 各个组件通信的授权相关的工作
ContainerAllocationExpirer  ---- 对过期没有使用Container进行回收.

AMLivelinessMonitor -- 监测ApplicationMaster 是否活着, 在AbstractLivelinessMonitor中启动一个线程,每1分钟发一次消息.
CommonNodeLabelsManager  --- 没弄懂, 暂时先放一放
RMApplicationHistoryWriter  -- 貌似是写历史信息的
SystemMetricsPublisher  -- 向timeline server发消息的, ResourceManager 会invoke它的相关方法, 无论它是否在线
NodesListManager --  没弄懂, 暂时先放一放
CapacityScheduler   -- YARN任务调度器
ResourceManager$SchedulerEventDispatcher  --  调度器事件的分发
NMLivelinessMonitor  -- Node Manager 监控
ResourceTrackerService -- -- 没弄懂, 暂时先放一放
SchedulingMonitor -- 调度监控, 如果一个container在target list中的等待时间超过了maxWaitTime, 就Kill掉, 已释放资源
ApplicationMasterService -- ApplicationMaster相关的服务
ApplicationMasterLauncher  -- 启动 Application 应用程序