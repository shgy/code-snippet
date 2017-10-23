ContainersLauncher用于启动/恢复/清理Container, 作为一个Service, 它的入口也是handle, 事件驱动。


ContainersLauncher有一个很重要的属性`public final Map<ContainerId, ContainerLaunch> running`

`LAUNCH_CONTAINER`: 启动Container  ---- running.put(containerId, launch)

`RECOVER_CONTAINER`: 恢复Container ---- running.put(containerId, launch)

`CLEANUP_CONTAINER`: 清理Container ---- running.remove(containerId);

到这里， 就多说一句， NM上Container的生命周期：

New --> Localizing --> Localized --> Running --> Exited_with_success --> Done.

这个是主线， 绝大部分Container都是这样普普通通过完自己的一生。

ContainersLauncher负责是的 Container的 `Running --> Exited_with_success`这段人生。

由于ContainerLaunch启动Container进程后，会处于阻塞状态， 等待Container进程的退出， 所以
先发送`ContainerEventType.CONTAINER_LAUNCHED`事件， 然后启动进程， 等Container正常结束后， 
再发送`ContainerEventType.CONTAINER_EXITED_WITH_SUCCESS`事件。


由于ContainersLauncher是事件驱动, 不能阻塞主流程， 所以ContainerLaunch是Callable类型，在新的线程中启动。 
这里可能会有这样一个问题：
ContainersLauncher在很短的时间内先后收到针对同一个Container的`LAUCH_CONTAINER`和`CLEANUP_CONTAINER`两个命令。
ContainerLauch对象就会在两个线程中执行，这样就有可能出现cleanupContainer()执行完了， Container进程却又启动的情况。
所以ContainerLauch使用了一个原子变量 `shouldLaunchContainer` 来避免这种情况发生。


再就是关于pid文件的内容了。

```
#!/bin/bash
echo $$ > test.pid
```

就可以生成pid文件，有了pid文件， 就不用费心查启动进程的pid了，管理起来就更方便了。

关于这个，需要回顾一下Linux shell的特殊位置参数：
```
$# 表示传递到脚本的参数数量
$*和$@ 表示传递到脚本的所有参数
$$ 表示脚本运行的进程号
$? 表示命令退出的状态
```
