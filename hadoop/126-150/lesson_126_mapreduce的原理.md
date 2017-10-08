mapreduce2.0作为yarn的app, 也必然遵循yarn的协议：

第3次回顾一下 Yarn-App需要用到的3个RPC协议：

1. ApplicationClientProtocol:              Client  --> ResourceManager  submitApp/forceKillApp/monitorApp

2. ApplicationMasterProtocol:   ApplicationMaster  --> ResourceManager  registerApp/finishApp

3. ContainerManagementProtocol: ApplicationMaster  --> NodeManager      start/stop container



1. Client<-->ResourceManager
Client与ResourceManager的交互主要作用：申请资源， 启动AppMaster。看看Mapreduce中， Client是如何跟ResourceManager交互的呢？


ClientProtocolProvider用来构造ClientProtocol实现类YarnRunner，它直接产生一个RM代理ResourceMgrDelegate。
在MRv2中，ResourceMgrDelegate继承了YarnClientImpl抽象类（同时YarnClientImpl实现了YarnClient接口），
通过ApplicationClientProtocol代理直接向RM提交Job，杀死Job，查看Job运行状态等操作。
实际了上，只需要能过YarnClient.createYarnClient()静态方法就可以得到YarnClientImpl对象。
YarnClientImpl 中就有 ApplicationClientProtocol 的一席之地了。




参考： http://zcdeng.iteye.com/blog/1897116

2. ApplicationMaster  --> ResourceManager  协议： ApplicationMasterProtocol
AppMaster与RM交互， 才是任务执行的开始。  Client与RM的交互， 只能是任务的下达。 这一步， mapreduce是如何处理的呢？

RMAppMaster类的成员`ContainerAllocator`，其实现类， 无论是`RMContainerAllocator`还是`LocalContainerAllocator`
都实现abstract类`RMCommunicator`。 而`RMCommunicator` 中， 就有成员变量`scheduler`, 
它就是`ApplicationMasterProtocol`的实现类。

这里涉及到Mapreduce的uber模式。  

Uber运行模式对小作业进行优化，不会给每个任务分别申请分配Container资源，这些小任务将统一在一个Container中按照先
执行map任务后执行reduce任务的顺序串行执行。那么什么样的任务，mapreduce框架会认为它是小任务呢？

1.map任务的数量不大于mapreduce.job.ubertask.maxmaps参数（默认值是9）的值;
1.reduce任务的数量不大于mapreduce.job.ubertask.maxreduces参数（默认值是1）的值;
1.输入文件大小不大于mapreduce.job.ubertask.maxbytes参数（默认为1个Block的字节大小）的值；
1.map任务和reduce任务需要的资源量不能大于MRAppMaster（mapreduce作业的ApplicationMaster）可用的资源总量


3. ApplicationMaster  --> NodeManager 协议: ContainerManagementProtocol

AppMaster与NM交互， 意味这任务的运行。 

RMAppMaster的成员变量`containerLauncher`， 其实现为：ContainerLauncherImpl。 搜索关键词 `startContainers`
即可发现。


初步了解了一下mapreduce涉及的3个协议。 接下来了解：
1. Client是如何启动RMAppMaster.
2. RMAppMaster是如何启动MapTask和ReduceTask.
3. MapTask和ReduceTask的协作方式。



