本节关注 在什么情况下， RMApp的Attempt会增长：
``` 
  public boolean shouldCountTowardsMaxAttemptRetry() {
    try {
      this.readLock.lock();
      int exitStatus = getAMContainerExitStatus();
      return !(exitStatus == ContainerExitStatus.PREEMPTED
          || exitStatus == ContainerExitStatus.ABORTED
          || exitStatus == ContainerExitStatus.DISKS_FAILED
          || exitStatus == ContainerExitStatus.KILLED_BY_RESOURCEMANAGER);
    } finally {
      this.readLock.unlock();
    }
  }
```

貌似RMApp的重试， 关注的焦点在AMContainer的退出状态：
1. AMContainer如果被抢占， 
2. AMContainer被中止
3. 硬盘文件出错(文件过多)
4. RM手动kill
除以上4种，都会增长。 
