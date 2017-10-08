基本上了解了mapreduce框架的运行过程， 像 ”环形缓冲区“， ”Merge原理“这些更细节的知识点则没有深挖。
接下来先看看jira上记录的bug。 然后再学习了解hadoop-yarn的资源调度这一块的知识点。

看bug从CHANGES开始入手：
http://hadoop.apache.org/docs/r3.0.0-beta1/hadoop-project-dist/hadoop-common/release/3.0.0-beta1/CHANGES.3.0.0-beta1.html


链接： https://issues.apache.org/jira/browse/MAPREDUCE-6905

这个bug对于生产环境的影响不大， 属于性能测试(基准测试)工具。 

这个Bug带来的问题是： 在进行基准测试时， 如果设定的nrFiles文件数过大，比如2000000， 则
代码在运行几个小时后才抛出异常。用户体验不好。 

bug修复的方法是， 在创建文件前 检测 nrFiles参数
``` 
+    final int maxDirItems = config.getInt(
+        DFSConfigKeys.DFS_NAMENODE_MAX_DIRECTORY_ITEMS_KEY,
+        DFSConfigKeys.DFS_NAMENODE_MAX_DIRECTORY_ITEMS_DEFAULT);
     Path controlDir = getControlDir(config);
+
+    if (nrFiles > maxDirItems) {
+      final String message = "The directory item limit of " + controlDir +
+          " is exceeded: limit=" + maxDirItems + " items=" + nrFiles;
+      throw new IOException(message);
+    }
```

题外话， 参考博客中提到了hadoop常用的性能测试工具， 是Terasort和SliveTest， TestDFSIO貌似比较鸡肋。


参考： 
http://blog.csdn.net/bigdatahappy/article/details/41844593