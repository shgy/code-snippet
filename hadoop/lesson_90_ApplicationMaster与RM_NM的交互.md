（1） 涉及到的RPC协议
通常而言，编写一个YARN Appcalition涉及到3个RPC协议，分别为：
1）  ClientRMProtocol(Client<–>ResourceManager)
Client通过该协议将应用程序提交到ResourceManager上、查询应用程序的运行状态或者杀死应用程序等。

2）  ApplicationMasterProtocol(ApplicationMaster<–>ResourceManager)
ApplicationMaster使用该协议向ResourceManager注册、申请资源以运行自己的各个任务。

3）  ContainerManager(ApplicationMaster<–> NodeManager)
ApplicationMaster使用该协议要求NodeManager启动/撤销Container，或者获取各个container的运行状态。


问题：
   假如， 设置启动10个container, applicationmaster占用container的会计算进去吗？ 不会
   10个container在Node机器上的分布是怎样的， 能否调整 ？ node_label_expression 可以调整
   为什么container会这样分布 ？
   可以任意指定applicationmaster运行的Node吗？ 可以。

先大致了解ResourceManager的内部功能模块， 再回头理清这些问题。


看《Apache Hadoop YARN: Moving beyond MapReduce and Batch Processing with Apache Hadoop 2》
chapter 7 ApplicationMaster一节讲解了container的调度。

参考:
http://dongxicheng.org/mapreduce-nextgen/how-to-write-an-yarn-applicationmaster/
