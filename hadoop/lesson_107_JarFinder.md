在`TestDistributedShell`类中， 需要模拟向Yarn提交application.  提交任务， 有一项很重要的准备工作就是将本地的jar文件
复制到HDFS中。

如果只启动了MiniYARNCluster, 那么HDFS默认就local_fs . 有一个需求就是：
将class文件打包成jar文件。 这就要用到JarFinder类。 需要注意的地方在于： 不能只打包一个class文件。
