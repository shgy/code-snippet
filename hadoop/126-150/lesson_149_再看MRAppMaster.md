假如自己开发了一个AppMaster， 如何确定AppMaster逻辑的正确呢？ 测试！

所以这次学习MRAppMaster, 从测试案例的角度切入。

1. testMRAppMasterForDifferentUser  --- 这个测试检测不同用户生成的staging目录。
2. testMRAppMasterMidLock 
3. testMRAppMasterSuccessLock
4. testMRAppMasterFailLock          --- 这3个是测试Lock

问题一： staging目录的作用？ 貌似staging是MRAppMaster的工作目录。
问题二： lock的作用 ？

CommitterEventHandler 会创建文件： COMMIT_STARTED/COMMIT_FAIL/COMMIT_SUCCESS 。 
``` 
 // If job commit is repeatable, then we should allow
    // startCommitFile/endCommitSuccessFile/endCommitFailureFile to be written
    // by other AM before.
    private void touchz(Path p, boolean overwrite) throws IOException {
      fs.create(p, overwrite).close();
    }

  protected void handleJobCommit(CommitterJobCommitEvent event) {
      boolean commitJobIsRepeatable = false;
      try {
        commitJobIsRepeatable = committer.isCommitJobRepeatable(
            event.getJobContext());
      } catch (IOException e) {
        LOG.warn("Exception in committer.isCommitJobRepeatable():", e);
      }

      try {
        touchz(startCommitFile, commitJobIsRepeatable);
        jobCommitStarted();
        waitForValidCommitWindow();
        committer.commitJob(event.getJobContext());
        touchz(endCommitSuccessFile, commitJobIsRepeatable);
        context.getEventHandler().handle(
            new JobCommitCompletedEvent(event.getJobID()));
      } catch (Exception e) {
        LOG.error("Could not commit job", e);
        try {
          touchz(endCommitFailureFile, commitJobIsRepeatable);
        } catch (Exception e2) {
          LOG.error("could not create failure file.", e2);
        }
        context.getEventHandler().handle(
            new JobCommitFailedEvent(event.getJobID(),
                StringUtils.stringifyException(e)));
      } finally {
        jobCommitEnded();
      }
    }
```

关于COMMIT_STARTED/COMMIT_FAIL/COMMIT_SUCCESS的作用。 我猜想在HDFS上通过文件锁的方式， 防止重复提交。



MRAppMaster不负责具体的MapReduce任务，负责整个job的调度。其需要完成的功能如下：
1. 向ResourceManager注册
2. 向ResourceManager申请和领取资源
3. 与对应的NodeManager通信， 启动任务
4. 各个NodeManager向MRAppMaster汇报任务的状态
5. 向ResourceManager注销