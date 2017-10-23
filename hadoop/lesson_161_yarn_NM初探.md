其实在前面已有几篇NM的学习笔记。现在翻看， 觉得杂乱无章。 

1. 调度器会处理一个事件： NODE_ADDED, 在NM端， 由NodeStatusUpdater负责。

2. NodeStatusUpdater 是最后一个启动的服务， 这是因为NodeStatusUpdater会向RM注册所在的NodeManager, 
   在此之前， 必须先确保NodeManager的所有服务启动成功。
   
3. NM的核心在于管理Container的生命周期。

4. NM会与RM通信， 也会跟各个应用的AM通信。
   跟RM通信时， RM是老大(Server端)， 即所谓的pull模型； 跟AM通信时， NM是老大(Server端)， 即所谓的push模型。
   
5. NM维护了Application, Container, LocalizedResource 3个状态机。

6. NodeManager中包含两个中央异步调度器， 分别位于NodeManager和ContainerManagerImpl


初步结论： NM有两个入口， 一个是NodeStatusUpdater, 一个是ContainerManagerImpl。 
接下来从这两个组件入手，了解其内部运行的过程。 先调熟悉的入手： NodeStatusUpdater
