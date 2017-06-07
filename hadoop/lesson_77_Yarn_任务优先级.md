我们知道， Yarn的Application可以设定优先级。 优先级的原理是什么呢？

通过追踪TestContainerExecutor源代码，可以了解， ContainerExecutor执行程序的优先级是通过nice命令
实现的。
```
conf.setInt(YarnConfiguration.NM_CONTAINER_EXECUTOR_SCHED_PRIORITY, 2);
String[] command = containerExecutor.getRunCommand("echo", "group1", "user", null, conf);
```
nice的实现是`nice -n 2 bach echo`
