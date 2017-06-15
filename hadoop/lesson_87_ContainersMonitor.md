ContainersMonitorImpl 的功能 就是监控Container中启动的进程使用的内存是否超出限制,
其原理为读取linux的/proc文件系统

MonitoringThread 监控， 如果内存超出限制，则发送KILL_CONTAINER事件到队列。

随后 ContainerImpl.KillTransition 会发送 CLEANUP_CONTAINER事件到队列
```
 /**
   * Transitions upon receiving KILL_CONTAINER:
   * - LOCALIZED -> KILLING
   * - RUNNING -> KILLING
   */
  @SuppressWarnings("unchecked") // dispatcher not typed
  static class KillTransition implements
      SingleArcTransition<ContainerImpl, ContainerEvent> {
    @Override
    public void transition(ContainerImpl container, ContainerEvent event) {
      // Kill the process/process-grp
      container.dispatcher.getEventHandler().handle(
          new ContainersLauncherEvent(container,
              ContainersLauncherEventType.CLEANUP_CONTAINER));
      ContainerKillEvent killEvent = (ContainerKillEvent) event;
      container.addDiagnostics(killEvent.getDiagnostic(), "\n");
      container.exitCode = killEvent.getContainerExitStatus();
    }
  }
```
ContainersLauncher.handle()
   --> ContainerLaunch.cleanupContainer()
      --> DelayedProcessKiller.run()
          --> DefaultContainerExecutor.signalContainer()
              --> ShellCommandExecutor().execute()
                 --> Shell.runCommand()
                    --> java.lang.ProceccorBuilder()

   最终， 不过是执行 `kill` 命令。

整个链条， ContainerExecutor 是很关键的一环。 再查看ContainerExecutor的源码可知，
ContainerExecutor 可谓掌管这着Container的“生杀大权”
