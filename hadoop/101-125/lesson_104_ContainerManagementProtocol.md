ContainerManagementProtocol在NodeManager端的实现类为： ContainerManagerImpl

用于ApplicationMaster与NodeManager之间通信。

该协议只有3个方法， 功能相当明确：

`startContainers`
  
  在NodeManager上启动container.
  1. 创建ContainerImpl对象， 并保存到context中。
  2. 创建ApplicationImpl对象，并保存到context中。
  3. 如果ApplicationImpl不存在， 则该Container是AppMaster, 发送ApplicationInitEvent事件。
  4. 发送ApplicationContainerInitEvent事件。
  
  
  接下来的处理逻辑在ApplicationImpl类中。
  
    如果 ApplicationImpl不存在
       ApplicationImp接收ApplicationInitEvent, 发送LogHandlerAppStartedEvent事件
       从New状态转移到INITING状态。
  
    将新创建的ContainerImpl添加到app.containers对象中。
    如果app处于RUNNING状态， 则发送ContainerInitEvent
  
  
  接下来的处理逻辑在ContainerImpl类中。
     
     1. 准备好Container执行的资源
     2. 发出ContainersLauncherEvent事件
  
  接下来的处理逻辑在ContainersLauncher类中。
     
     1. 接收ContainersLauncherEvent事件。 创建ContainerLaunch对象，并提交
        在ContainerLaunch.run()方法中， ContainerExecutor会启动Container.
        也就是启动一个子进程。

  点评： Container启动的准备工作还是挺繁琐的，特别是资源的localization.
  

`stopContainers`
  
    1. 发送ContainerKillEvent事件。
    
    Container的Kill可能发生在各个状态。 ContainerImpl分门别类进行处理。
    对于处于
     * - LOCALIZED -> KILLING
     * - RUNNING -> KILLING
    发送`ContainersLauncherEventType.CLEANUP_CONTAINER`事件
    对于处于`LOCALIZING`状态的Container, 调用container.clean()  

  
  