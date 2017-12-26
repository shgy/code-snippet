
YARN-2834: RM重启后失败
   如果 app 更新token失败, app会转移到 FAILED 状态, 并且将app的最终状态保存. 但是RMAppAttempt
依然正常运行. 因此,在RM重启后, app由于处于失败状态,不会添加到调度器中, 但是 attempt会进行调度,所以
attempt会找不到app, 从而抛出NPE (Null Pointer Exception)
   
   如何修复呢? 这个涉及到RM的个各个组件的功能.  在
ApplicationMasterService 和 ApplicationMasterLauncher 两个组件中找找. 
ApplicationMasterService用户 RM-AM交互, AM向RM证明自己活着, 领取生活费. 
在RMAppManager找到了.




ResourceManager在启动的时候, 如果RM支持 recover 功能(可配置), 则会在RM端进行recover操作.

RM的重启需要做到:
1. 不影响集群的正常运转(keep functioning)
2. 对终端用户是透明的(终端用户不知道有重启这回事儿)

想想还是挺难的: 一个集群, RM相当一个中枢. 集群越大, 维护的NM和AppMaster越多. 在重启的时候, 各个NM的连接, 心跳如何处理?


RM的重启特性的发展, 经历了两个阶段:
阶段一:  强化运行状态的持久化功能. (2.4.0)
        将正在运行application/attempt 的状态 及其他的 验证信息 保留起来. RM重启后, 利用这些信息自动重启app, 不需要用户重新提交.
       (这种处理方式, 对集群的资源有一定的浪费, 如果一个比较耗时的任务, 快要结束了, RM重启这个任务会自动重跑一遍)
        RM重启后, NM会接到re-sync命令, 将NM上运行的AM kill掉.
        
        
阶段二: 关注如何基于NM/AM上报, 重构RM上各个app的运行状态. (2.6.0) 
      跟阶段一相比, 运行的application在RM重启后不会被kill. 这些applications不会由于RM的中断而使之前的工作白费.
      由于阶段一已经做了 application/attempt的持久化工作, 阶段二只需解决application运行状态重构的问题.
      
      RM的核心组件 调度器会记录 Container的生命周期, applications的资源剩余及资源申请信息, 队列资源的使用量等信息.因此,RM不必像
      阶段一那样重跑application.  只需要结合重启前RM中application的快照(阶段一存储), 重启后NM的上报即可恢复application的运行
      状态, 需要注意的是AM需要重新将RM重启期间没有处理的资源请求发送给RM. 当然这个功能AMRMClient库已经实现, 不必重复造轮子.

关于RM的state-store组件: 有4种: 
```
org.apache.hadoop.yarn.server.resourcemanager.recovery.ZKRMStateStore, a ZooKeeper based state-store implementation and 
org.apache.hadoop.yarn.server.resourcemanager.recovery.FileSystemRMStateStore, a Hadoop FileSystem based state-store implementation like HDFS and local FS. 
org.apache.hadoop.yarn.server.resourcemanager.recovery.LeveldbRMStateStore, a LevelDB based state-store implementation. The default value is set to 
org.apache.hadoop.yarn.server.resourcemanager.recovery.FileSystemRMStateStore.
```

其实: 理解了上面的信息, state-store组件的功能大概是可以猜出来的. 这个就是所谓的 提纲挈领 吧! 所以: 理解需求是重点, 谋定而后动.

参考: 
https://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/ResourceManagerRestart.html
