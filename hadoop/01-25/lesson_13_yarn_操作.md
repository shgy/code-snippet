执行如下的命令:
```
yarn jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.6.0.jar pi 16 1000
```
运行到最后, 得到的结果是

```
Job Finished in 5.367 seconds
Estimated value of Pi is 3.14250000000000000000
```

步 骤 1  用户向YARN中提交应用程序, 其中包括ApplicationMaster程序、启动ApplicationMaster的命令、用户程序等。

步 骤 2 ResourceManager 为 该 应 用 程 序 分 配 第 一 个 Container, 并 与 对 应 的 Node-
Manager 通信,要求它在这个 Container 中启动应用程序的 ApplicationMaster。
(类似于RM给AM一封介绍信, 说你去找它吧, 它会给你所需要的)

步 骤 3 ApplicationMaster 首先向ResourceManager注册, 这样用户可以直接通过
ResourceManage 查看应用程序的运行状态,然后它将为各个任务申请资源,并监控它的运
行状态,直到运行结束,即重复步骤 4~7。
(AM向RM申请了一个GPS, 这样AM走到哪里RM就能看见. 妈妈再也不担心我学习了)

步骤 4 ApplicationMaster 采用轮询的方式通过 RPC 协议向 ResourceManager 申请和
领取资源。
步骤 5 一旦 ApplicationMaster 申请到资源后,便与对应的 NodeManager 通信,要求
它启动任务。
步骤 6 NodeManager 为任务设置好运行环境(包括环境变量、JAR 包、二进制程序
等)后,将任务启动命令写到一个脚本中,并通过运行该脚本启动任务。
步骤 7 各个任务通过某个 RPC 协议向 ApplicationMaster 汇报自己的状态和进度,以
让 ApplicationMaster 随时掌握各个任务的运行状态,从而可以在任务失败时重新启动任务。
在应用程序运行过程中,用户可随时通过 RPC 向 ApplicationMaster 查询应用程序的当
前运行状态。
步骤 8 应用程序运行完成后, ApplicationMaster 向 ResourceManager 注销并关闭自己。