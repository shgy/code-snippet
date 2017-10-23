YarnScheduler 也是一个事件处理器(EventHandler).  

  | 事件  | 发送时机 | 处理逻辑 |
  | -----| --------|---------|  
  | NODE_ADDED     | 一个NM被添加          | 增加总资源池的大小，修改内存状态。   | 
  | NODE_REMOVED   | 一个NM被移除          |  删除一个NM,减少总资源池的大小，回收内存状态中在这个NM上的Container，对每个container发送KILL事件。 | 
  | NODE_UPDATE    | 一个NM 跟RM进行心跳    |  调度器会根据当前的NM状况，在这个NM上为某一个AM分配Container，并记录这个Container的信息， 留待AM获取。这个部分是调度器真正分配container的部分。后面会重点描述。 | 
  | APP_ADDED      | 一个新的 应用被提交     | 如果接受应用，发送APP_ACCEPTED事件，否则发送APP_REJECTED事件。|    
  | APP_REMOVED    | 一个应用移除           | 可能是正常或者被杀死。清除内存中这个应用的所有的container，对每个container发送KILL事件。   | 
  | CONTAINER_EXPIRED  | 一个container过期未被使用   | 修改内存中这个contianer相关的内存状态。| 
  
 
首先关注 `NODE_UPDATE`事件， 该事件是NM发送周期性心跳时触发的。 
1. 调度器会处理NM的心跳带来那些那些信息呢？ 
   newlyLaunchedContainers,   -- 代表NM上新启动起来的Container
   completedContainers        -- 代表NM上已经完成的Container

对于新启动起来的Container， 需要通知`ContainerAllocationExpirer`, 以免该Container被回收。

所以这条线如下：
FifoScheduler.handle() -- NODE_UPDATE
--> FifoScheduler.nodeUpdate() 
--> FifoScheduler(AbstractYarnScheduler).containerLaunchedOnNode()
--> SchedulerApplicationAttempt.containerLaunchedOnNode()
--> RMContainerImpl.handle() -- RMContainerEventType.LAUNCHED事件
--> RMContainerImpl.LaunchedTransition.transition()
--> ContainerAllocationExpirer.unregister()

即最后ContainerAllocationExpirer 从running队列中移除该container.

---------------------------------------------------------------------------

对于已经完成的Container， 需要从node和app两个维度来处理。

从node的维度比较简单， SchedulerNode对象中除去container，且回升资源量即可。 注： SchedulerNode是从调度器角度关注的Node.
从app的维度稍显复杂： 
  1. FiCaSchedulerApp对象中除去container
  2. 发送RMContainerEventType.FINISHED事件给RMContainerImpl.
  3. RMContainerImpl 发送RMAppAttemptEventType.CONTAINER_FINISHED事件给RMAppAttemptImpl.
  4. RMAppAttemptImpl.ContainerFinishedTransition 处理事件。 
  (感觉这里没有清晰的脉络)
 
 
 
 每次NM心跳的时候，调度器根据一定的规则选择一个队列，再在队列上选择一个应用，尝试在这个应用上分配资源
 
 以MapReduce为例， 了解调度的基本过程。
 
 MapReduce是离线应用， 在提交前， 应该已经计算出来的MapTask的个数， ReduceTask的个数 等信息， 
  因此需要启动的Container个数也是一定的。
 但是有个问题: Java程序运行需要多少内存， 这个限定死了可能有问题。Yarn是怎么解决这个问题的...



 
一. AppMaster得到Container的时机 (倒推)
 
 1.  RMAppAttemptImpl.AMContainerAllocatedTransition() 即RMAppAttemptImpl状态机接收到了RMAppAttemptEventType.CONTAINER_ALLOCATED事件。
 
 2. RMContainerImpl.ContainerStartedTransition()  即RMContainerImpl状态机接收到了 RMContainerEventType.START 事件。
 
 -- 调度器
 3. FiCaSchedulerApp.allocate()方法(最新版该对象名称为FifoAppAttempt) 被调用，`rmContainer.handle(new RMContainerEvent(containerId, RMContainerEventType.START));`
 
 4. FifoScheduler.assignContainer()
 
 5. FifoScheduler.assignNodeLocalContainers()
 
 6. FifoScheduler.assignContainersOnNode()
 
 7. FifoScheduler.assignContainers()
 
 8. FifoScheduler.nodeUpdate()
 
 9. FifoScheduler.handle()
 

二. AppMaster启动的过程

--> AMLauncher.launch()

--> LauncherThread.run() -- 异步， 在一个线程中执行

--> ApplicationMasterLauncher.launch()

--> ApplicationMasterLauncher.handle() -- 异步事件

--> RMAppAttemptImpl.launchAttempt() -- 发送异步事件AMLauncherEventType.LAUNCH事件

--> RMAppAttemptImpl.AttemptStoredTransition()  -- 接收到RMAppAttemptEventType.ATTEMPT_NEW_SAVED事件

--> RMStateStore.notifyApplicationAttempt()

--> RMStateStore.StoreAppAttemptTransition() -- 接收到RMStateStoreEventType.STORE_APP_ATTEMPT事件

--> RMStateStore.storeNewApplicationAttempt()

--> RMAppAttemptImpl.storeAttempt()

--> RMAppAttemptImpl.AMContainerAllocatedTransition() -- 接收到RMAppAttemptEventType.CONTAINER_ALLOCATED事件

--> RMContainerImpl.ContainerStartedTransition() 

--> FiCaSchedulerApp.allocate()

花开两朵， 各表一枝， 从 `FiCaSchedulerApp.allocate()` 方法开始。



参考; https://wenku.baidu.com/view/34c3529a804d2b160a4ec0b4.html