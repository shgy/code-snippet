上一节实现了基于Yarn App的Hello World, 其重点在于理解Container的启动流程，并成功实现了启动一个Container。
本节通过MRAppMaster的源码， 来验证上一节知识的通用性和关联性。

启动一个Container， 分3步： 准备资源(LocalResource) --> 设置环境变量(env)  --> 构造启动命令(commmand)

在MapReduce的框架中， 这3步的代码在哪里呢？
`org.apache.hadoop.mapreduce.v2.app.job.impl.TaskAttemptImpl.createCommonContainerLaunchContext()`方法。

在TEZ框架中， 这3步的代码在哪里呢？
`org.apache.tez.dag.app.rm.container.AMContainerHelpers.createContainerLaunchContext()`方法。

在Spark的框架中， 也遵循这样的工作模式 。。。 (不懂scala的代码,只做记录)
`resource-managers/yarn/src/main/scala/org/apache/spark/deploy/yarn/ApplicationMaster.scala`