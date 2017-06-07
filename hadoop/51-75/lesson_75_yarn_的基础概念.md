
服务库， 事件库， 状态机库

yarn脱胎与JobTracker类。 JobTracker原本有两个功能：
1. 资源管理     -- ResourceManager
2. 任务进度追踪 -- ApplicationMaster

hadoop-yarn-applications-distributedshell样例分析。

为了学习开发基于yarn的应用， 比如：mapreduce/tez/spark, 从最简单的distributedshell入手。


yarn应用的提交过程：

YarnClient.submitApplication()
  --> ApplicationClientProtocolPBClientImpl.submitApplication()
    (RPC)
    --> ApplicationClientProtocolPBServiceImpl.submitApplication()
       --> ClientRMService.submitApplication()
         --> RMAppManager.submitApplication()
           (事件)
            --> AsyncDispatcher.GenericEventHandler.handle()
               (放入到事件队列中，等待消费者消费)

事件类型 及 消费者

RMFatalEventType                      for class .ResourceManager$RMFatalEventDispatcher
recovery.RMStateStoreEventType        for class .recovery.RMStateStore$ForwardingEventHandler
NodesListManagerEventType             for class .NodesListManager
scheduler.event.SchedulerEventType    for class .ResourceManager$SchedulerEventDispatcher
rmapp.RMAppEventType                  for class .ResourceManager$ApplicationEventDispatcher
rmapp.attempt.RMAppAttemptEventType   for class .ResourceManager$ApplicationAttemptEventDispatcher
rmnode.RMNodeEventType                for class .ResourceManager$NodeEventDispatcher
RMAppManagerEventType                 for class .RMAppManager
amlauncher.AMLauncherEventType        for class .amlauncher.ApplicationMasterLauncher
rmapp.attempt.RMAppAttemptEventType   for class .MiniYARNCluster$1
container.ContainerEventType          for class .ContainerManagerImpl$ContainerEventDispatcher
application.ApplicationEventType      for class .ContainerManagerImpl$ApplicationEventDispatcher
localizer.event.LocalizationEventType for class .localizer.ResourceLocalizationService
AuxServicesEventType                  for class .AuxServices
monitor.ContainersMonitorEventType    for class .monitor.ContainersMonitorImpl
launcher.ContainersLauncherEventType  for class .launcher.ContainersLauncher
ContainerManagerEventType             for class .ContainerManagerImpl
NodeManagerEventType                  for class .MiniYARNCluster$ShortCircuitedNodeManager
loghandler.event.LogHandlerEventType  for class .loghandler.NonAggregatingLogHandler
localizer.event.LocalizerEventType    for class .localizer.ResourceLocalizationService$LocalizerTracker

