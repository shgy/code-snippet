container.ContainerEventType          for ContainerManagerImpl$ContainerEventDispatcher
application.ApplicationEventType      for ContainerManagerImpl$ApplicationEventDispatcher
localizer.event.LocalizationEventType for localizer.ResourceLocalizationService
AuxServicesEventType                  for AuxServices
monitor.ContainersMonitorEventType    for monitor.ContainersMonitorImpl
launcher.ContainersLauncherEventType  for launcher.ContainersLauncher
localizer.event.LocalizerEventType    for localizer.ResourceLocalizationService$LocalizerTracker
loghandler.event.LogHandlerEventType  for loghandler.NonAggregatingLogHandler


以ContainerImpl类为例， 学习yarn的两个核心的编程思想: 事件驱动和状态机
1. ContainerEventDispatcher
   1. ContainerImpl.handle()
   2. ContainerImpl.RequestResourcesTransition.transition()

这里有个比较绕的地方: 事件的发生触发状态的变化； 状态的变化又生成新的事件。


ContainerEventDispatcher类处理container.ContainerEventType事件类型。
```
public enum ContainerEventType {

  // Producer: ContainerManager
  INIT_CONTAINER,
  KILL_CONTAINER,
  UPDATE_DIAGNOSTICS_MSG,
  CONTAINER_DONE,

  // DownloadManager
  CONTAINER_INITED,
  RESOURCE_LOCALIZED,
  RESOURCE_FAILED,
  CONTAINER_RESOURCES_CLEANEDUP,

  // Producer: ContainersLauncher
  CONTAINER_LAUNCHED,
  CONTAINER_EXITED_WITH_SUCCESS,
  CONTAINER_EXITED_WITH_FAILURE,
  CONTAINER_KILLED_ON_REQUEST,
}
```
它的处理过程就是修改Container的`stateMachine`状态。 stateMachine是事先就构建好了的, (代码参考ContainerImpl类163~334)

