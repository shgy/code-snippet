The NM restart work preserving feature could make running AM container get LOST and killed during stop NM daemon. 
In reacquireContainer() of ContainerExecutor.java, the while loop of checking container process (AM container) 
will be interrupted by NM stop. The IOException get thrown and failed to generate an ExitCodeFile for 
the running container. Later, the IOException will be caught in upper call (RecoveredContainerLaunch.call()) 
and the ExitCode (by default to be LOST without any setting) get persistent in NMStateStore. 
After NM restart again, this container is recovered as COMPLETE state but exit code is LOST (154) - 
cause this (AM) container get killed later.
We should get rid of recording the exit code of running containers if detecting process is interrupted.
什么情况下会重启NM呢？ 集群的滚动升级(rolling upgrades)

如何判断一个进程是否活着： 
```
“signal 0″ is kind of like a moral equivalent of “ping”.
Using “kill -0 NNN” in a shell script is a good way to tell if PID “NNN” is alive or not:
signal 0 is just used to check process is exists or not.
```
将退出状态写入到  pid.exitcode 文件
``` 
 public void writeLocalWrapperScript(Path launchDst, Path pidFile,
        PrintStream pout) {
      String exitCodeFile = ContainerLaunch.getExitCodeFile(
          pidFile.toString());
      String tmpFile = exitCodeFile + ".tmp";
      pout.println("#!/bin/bash");
      pout.println("/bin/bash \"" + sessionScriptPath.toString() + "\"");
      pout.println("rc=$?");
      pout.println("echo $rc > \"" + tmpFile + "\"");
      pout.println("/bin/mv -f \"" + tmpFile + "\" \"" + exitCodeFile + "\"");
      pout.println("exit $rc");
    }

```

如果 进程 被中断： 退出码将不会被记录。

两个问题： 
1. NM重启时， 正在运行的进程如何处理？
2. NM重启后， NM重启时杀掉的进程是否会恢复， 如何恢复？

这两个问题 参考： lesson_176