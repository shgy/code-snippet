
Application 状态机/ Container 状态机 / Localized Resource 状态机

NEW --> INITING --> RUNNTING
    --> FINISHING_CONTAINERS_WAIT --> APPLICATION_RESOURCES_CLEANUP --> FINSHED

INIT --> DOWNLOADING --> LOCALIZED

NEW --> LOCALIZING --> LOCALIZED --> RUNNING --> EXITED_WITH_SUCCESS --> DONE

生命周期由状态机维护。

TestContainer.testLocalizationRequest():

通过向中央异步调度器发送事件， 实现了服务之间的松耦合。 每个服务， 处理一类事件， 然后输出一类事件。

接下来， 就是要弄清楚各个事件的来源。

ContainerImpl.handle() -- ContainerEventType.INIT_CONTAINER
                      AuxServicesEvent.CONTAINER_INIT
