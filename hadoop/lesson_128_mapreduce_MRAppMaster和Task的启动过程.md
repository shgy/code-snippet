前面`lesson_110_YarnApp_hello.md`已经了解到, Yarn启动Container需要3步：
1. 设置container启动需要的资源
2. 设置AppMaster启动的环境上下文, 启动命令。
3. 提交到NodeManager, 启动Container

比如： mapreduce中Client启动MRAppMaster, 
相关的代码见`YarnRunner.createApplicationSubmissionContext()`。

以`org.apache.hadoop.examples.WordCount`为例， 追踪这个线条。
WordCount.main()
-->Job.waitForCompletion()
-->Job.submit()
-->JobSubmitter.submitJobInternal()
-->YarnRunner.submitJob()  --- YarnRunner implements ClientProtocol 
------>YarnUnner.createApplicationSubmissionContext() --- 这里设置RMAppMaster的启动3步骤。
--> resMgrDelegate.submitApplication()
--> YarnClientImpl.submitApplication()
--> ApplicationClientProtocol.submitApplication()    ---  这里出现了Yarn 应用的第一个Protocol (Client -> RM)

之后， RM会选择NM来启动MRAppMaster
MRAppMaster.main()
--> MRAppMaster.initAndStartAppMaster()
--> MRAppMaster.serviceStart()
--> MRAppMaster.startJobs()

后面的就感觉有点乱了， 需要运行代码才能确认。

这里涉及到了 mapreduce的执行流程， 已经不再受Yarn执行规范的限制了， 或者说与Yarn的关联更少了。