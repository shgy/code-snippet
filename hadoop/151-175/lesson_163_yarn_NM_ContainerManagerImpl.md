
前面了解到， NM的心跳过程中， RM会将需要清理的Container和App返回给NM, NodeStatusUpdater会向ContainerManagerImpl
发送`ContainerManagerEventType.FINISH_CONTAINERS`和`ContainerManagerEventType.FINISH_APPS`事件。

ContainerManagerImpl的操作如下：

一. ContainerManagerEventType.FINISH_CONTAINERS
 1.1 `this.context.getNMStateStore().storeFinishedApplication(appID);` 保存状态信息。
 1.2 发送ApplicationEventType.FINISH_APPLICATION事件给ApplicationImpl
     如果有container, 则向ContainersLauncher发送kill指令， 杀Container进程。
     否则， 清理App的资源。
 
二. `ContainerManagerEventType.FINISH_CONTAINERS` 
     向ContainersLauncher发送kill指令， 杀Container进程。

这里有一个问题， 接收这两个事件都有可能 让Container死于非命， 而非正常终结。



ContainerManagerImpl处理任务， 有两个来源： 事件处理器handle 和 RPC .

 
每个App都有一个ApplicationImpl管理， 每个Container都有一个ContainerImpl管理。


NM在启动时，并没有Container运行，App和Container是从哪里来的呢？ 

ContainerManager.startContainers()

1. 如果该Container是App在该NM上运行的第一个Container， 则创建ApplicationImpl对象(状态机)， 
   并发送ApplicationEventType.INIT_APPLICATION事件给ApplicationImpl。

2. 发送ApplicationEventType.INIT_CONTAINER事件给ApplicationImpl。


ContainerManager.stopContainers()
  向ContainersLauncher发送kill指令， 杀Container进程。