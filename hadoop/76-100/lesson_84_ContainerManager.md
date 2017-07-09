ContainerManager是NodeManager的核心服务。 再回顾一下Yarn的产生背景：
The fundamental idea of MRv2 is to split up the two major functionalities of the JobTracker,
 resource management and job scheduling/monitoring, into separate daemons.

Yarn通过ResourceManager管理全局的资源， 应用的调度。通过NodeManager实现Job的执行。Container是任务执行的单元。

一个container正常的生命周期包括： New --> Localizing --> localized --> Running --> Exited_With_Success --> Done ;

1. ContainerMointor： 监控Container使用的内存是否超出限制。如果超出限制， 则发送Kill命令。

2. ContainerLocalizer --> FSDownload:
   将远程资源下载到工作节点的目录中。
