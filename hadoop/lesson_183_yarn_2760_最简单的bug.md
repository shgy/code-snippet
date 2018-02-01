https://issues.apache.org/jira/browse/YARN-2760

为什么要把这个Bug拎出来说呢? 它太有示范意义了.  它可以用来纠正我们对源码bug的偏见: 觉得提交源码Bug是很高大上的东西, 需要比较高的门槛. 其实不然.

这个bug的解决方案就是移除了两行注释文字:
``` 
Completely remove word 'experimental' from FairScheduler docs
```

```
diff --git hadoop-yarn-project/hadoop-yarn/hadoop-yarn-site/src/site/apt/FairScheduler.apt.vm hadoop-yarn-project/hadoop-yarn/hadoop-yarn-site/src/site/apt/FairScheduler.apt.vm
index dfcf902..2ae8722 100644
--- hadoop-yarn-project/hadoop-yarn/hadoop-yarn-site/src/site/apt/FairScheduler.apt.vm
+++ hadoop-yarn-project/hadoop-yarn/hadoop-yarn-site/src/site/apt/FairScheduler.apt.vm
@@ -151,8 +151,7 @@ Properties that can be placed in yarn-site.xml
 
  * <<<yarn.scheduler.fair.preemption>>>
 
-    * Whether to use preemption. Note that preemption is experimental in the current
-      version. Defaults to false.
+    * Whether to use preemption. Defaults to false.
 
  * <<<yarn.scheduler.fair.preemption.cluster-utilization-threshold>>>
  
```
