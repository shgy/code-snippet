以yarn的example `distributedshell`为例
```
$ hadoop jar share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.6.0.jar \
org.apache.hadoop.yarn.applications.distributedshell.Client \
--jar share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.6.0.jar  \
--shell_command ls \
--num_containers 10 \
--container_memory 350 \
--master_memory 350 \
--priority 10
```
hadoop 在客户端(执行该命令所在的机器)使用RunJar启动客户端
`org.apache.hadoop.yarn.applications.distributedshell.Client`后，
Yarn会找一个Node机器启动一个Container, 运行ApplicationMaster.

ApplicationMaster相当于MRv1中的JobTracker. 其职责如下：
1. 周期性地向ResourceManager汇报其存活状态。
2. 计算当前应用需要耗费的资源。
3. 将资源转换成ResourceRequests, 便于YARN scheduler识别。
4. 请求资源。
5. 使用NodeManager上的container执行application
6. 追踪，监控container
7. 对于异常的node/container，进行合理的处置。

由于不同类型的Yarn应用， 需求不同，因此ApplicationMaster需要用户自己维护。
这样的话， 编写基于Yarn的应用， 门槛就有点高了。