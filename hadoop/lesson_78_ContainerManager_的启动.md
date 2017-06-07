
1. 工作目录
2017-05-29 23:21:32,508 INFO  [main] containermanager.TestContainerManager (BaseContainerManagerTest.java:setup(162)) - Created localDir in /home/shgy/hadoop_workspace/hadoop/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-nodemanager/target/TestContainerManager-localDir
2017-05-29 23:21:32,509 INFO  [main] containermanager.TestContainerManager (BaseContainerManagerTest.java:setup(163)) - Created tmpDir in /home/shgy/hadoop_workspace/hadoop/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-nodemanager/target/TestContainerManager-tmpDir

2. 注册事件处理器
2017-05-29 23:21:32,711 INFO  [main] event.AsyncDispatcher (AsyncDispatcher.java:register(197)) - Registering class org.apache.hadoop.yarn.server.nodemanager.containermanager.container.ContainerEventType for class org.apache.hadoop.yarn.server.nodemanager.containermanager.ContainerManagerImpl$ContainerEventDispatcher
2017-05-29 23:21:32,715 INFO  [main] event.AsyncDispatcher (AsyncDispatcher.java:register(197)) - Registering class org.apache.hadoop.yarn.server.nodemanager.containermanager.application.ApplicationEventType for class org.apache.hadoop.yarn.server.nodemanager.containermanager.ContainerManagerImpl$ApplicationEventDispatcher
2017-05-29 23:21:32,723 INFO  [main] event.AsyncDispatcher (AsyncDispatcher.java:register(197)) - Registering class org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.LocalizationEventType for class org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ResourceLocalizationService
2017-05-29 23:21:32,727 INFO  [main] event.AsyncDispatcher (AsyncDispatcher.java:register(197)) - Registering class org.apache.hadoop.yarn.server.nodemanager.containermanager.AuxServicesEventType for class org.apache.hadoop.yarn.server.nodemanager.containermanager.AuxServices
2017-05-29 23:21:32,729 INFO  [main] event.AsyncDispatcher (AsyncDispatcher.java:register(197)) - Registering class org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor.ContainersMonitorEventType for class org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor.ContainersMonitorImpl
2017-05-29 23:21:32,732 INFO  [main] event.AsyncDispatcher (AsyncDispatcher.java:register(197)) - Registering class org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher.ContainersLauncherEventType for class org.apache.hadoop.yarn.server.nodemanager.containermanager.launcher.ContainersLauncher
2017-05-29 23:21:32,785 INFO  [main] event.AsyncDispatcher (AsyncDispatcher.java:register(197)) - Registering class org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.event.LocalizerEventType for class org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer.ResourceLocalizationService$LocalizerTracker
2017-05-29 23:21:32,743 INFO  [main] event.AsyncDispatcher (AsyncDispatcher.java:register(197)) - Registering class org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.event.LogHandlerEventType for class org.apache.hadoop.yarn.server.nodemanager.containermanager.loghandler.NonAggregatingLogHandler

3.
2017-05-29 23:21:32,741 INFO  [main] nodemanager.NodeStatusUpdaterImpl (NodeStatusUpdaterImpl.java:serviceInit(175)) - Initialized nodemanager for null: physical-memory=8192 virtual-memory=17204 virtual-cores=8
2017-05-29 23:21:32,844 INFO  [main] nodemanager.NodeStatusUpdaterImpl (NodeStatusUpdaterImpl.java:registerWithRM(304)) - Registered with ResourceManager as null with total resource of <memory:8192, vCores:8>
2017-05-29 23:21:32,845 INFO  [main] nodemanager.NodeStatusUpdaterImpl (NodeStatusUpdaterImpl.java:registerWithRM(306)) - Notifying ContainerManager to unblock new container-requests

2017-05-29 23:21:32,745 INFO  [main] localizer.ResourceLocalizationService (ResourceLocalizationService.java:validateConf(216)) - per directory file limit = 8192
2017-05-29 23:22:33,118 INFO  [Public Localizer] localizer.ResourceLocalizationService (ResourceLocalizationService.java:run(840)) - Public cache exiting
2017-05-29 23:22:11,633 INFO  [main] localizer.ResourceLocalizationService (ResourceLocalizationService.java:serviceStart(341)) - Localizer started on port 8040

2017-05-29 23:21:32,796 INFO  [main] monitor.ContainersMonitorImpl (ContainersMonitorImpl.java:serviceInit(101)) -  Using ResourceCalculatorPlugin : org.apache.hadoop.yarn.util.LinuxResourceCalculatorPlugin@5936f59f
2017-05-29 23:21:32,797 INFO  [main] monitor.ContainersMonitorImpl (ContainersMonitorImpl.java:serviceInit(106)) -  Using ResourceCalculatorProcessTree : null
2017-05-29 23:21:32,798 INFO  [main] monitor.ContainersMonitorImpl (ContainersMonitorImpl.java:serviceInit(136)) - Physical memory check enabled: true
2017-05-29 23:21:32,801 INFO  [main] monitor.ContainersMonitorImpl (ContainersMonitorImpl.java:serviceInit(137)) - Virtual memory check enabled: true
2017-05-29 23:22:33,113 WARN  [Container Monitor] monitor.ContainersMonitorImpl (ContainersMonitorImpl.java:run(476)) - org.apache.hadoop.yarn.server.nodemanager.containermanager.monitor.ContainersMonitorImpl is interrupted. Exiting.

2017-05-29 23:21:59,939 INFO  [main] containermanager.ContainerManagerImpl (ContainerManagerImpl.java:serviceStart(420)) - Blocking new container-requests as container manager rpc server is still starting.
2017-05-29 23:22:14,121 INFO  [main] containermanager.ContainerManagerImpl (ContainerManagerImpl.java:serviceStart(465)) - ContainerManager started at shgy-thinkpad/127.0.0.1:12345
2017-05-29 23:22:15,086 INFO  [main] containermanager.ContainerManagerImpl (ContainerManagerImpl.java:serviceStart(466)) - ContainerManager bound to 0.0.0.0/0.0.0.0:12345
2017-05-29 23:22:24,469 INFO  [main] containermanager.ContainerManagerImpl (ContainerManagerImpl.java:getContainerStatusInternal(1008)) - Getting container-status for container_0_0000_01_000000
20
2017-05-29 23:21:32,808 INFO  [main] nodemanager.NodeStatusUpdaterImpl (NodeStatusUpdaterImpl.java:getNMContainerStatuses(416)) - Sending out 0 NM container statuses: []
2017-05-29 23:21:32,822 INFO  [main] nodemanager.NodeStatusUpdaterImpl (NodeStatusUpdaterImpl.java:registerWithRM(255)) - Registering with RM using containers :[]

2017-05-29 23:21:32,842 INFO  [main] security.NMContainerTokenSecretManager (NMContainerTokenSecretManager.java:setMasterKey(138)) - Rolling master-key for container-tokens, got key with id 123
2017-05-29 23:21:32,844 INFO  [main] security.NMTokenSecretManagerInNM (NMTokenSecretManagerInNM.java:setMasterKey(135)) - Rolling master-key for container-tokens, got key with id 123
2017-05-29 23:21:57,695 INFO  [main] ipc.CallQueueManager (CallQueueManager.java:<init>(53)) - Using callQueue class java.util.concurrent.LinkedBlockingQueue
2017-05-29 23:21:57,851 INFO  [main] pb.RpcServerFactoryPBImpl (RpcServerFactoryPBImpl.java:createServer(174)) - Adding protocol org.apache.hadoop.yarn.api.ContainerManagementProtocolPB to the server
2017-05-29 23:22:11,206 INFO  [main] security.NMContainerTokenSecretManager (NMContainerTokenSecretManager.java:setNodeId(260)) - Updating node address : localhost:12345
2017-05-29 23:22:33,144 INFO  [main] nodemanager.DefaultContainerExecutor (DefaultContainerExecutor.java:deleteAsUser(457)) - Deleting absolute path : /home/shgy/hadoop_workspace/hadoop/hadoop-yarn-project/hadoop-yarn/hadoop-yarn-server/hadoop-yarn-server-nodemanager/target/TestContainerManager-localDir
