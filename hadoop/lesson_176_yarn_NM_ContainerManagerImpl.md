其实在lesson_163已经提到ContainerManagerImpl的部分功能, 
但是很显然对ContainerManager的作用理解很浅薄。 看yarn-2864的bug， 重新查漏补缺。 

1. ContainerManagerImpl的启动(serviceInit)
   
   我们关注serviceInit的最后一个调用方法`recover()`, 这里会从NMStateStore中获取需要恢复的Container.
   那问题来了： 需要恢复的Container是何时存储到NMStateStore中去的呢？ 这个问题先挂着。
   
2. ContainerManagerImpl的停止(serviceStop)

   1. 不再接收启动Container的需求(下班了， 停止服务)
   2. 如果支持app恢复且NM没有退役(只是重启)， 则不清理app. 留待下次重启的时候恢复。


