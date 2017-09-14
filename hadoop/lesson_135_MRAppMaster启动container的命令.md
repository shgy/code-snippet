Client启动MRAppMaster时， 在YarnRunner.createApplicationSubmissionContext()方法中设置了MRAppMaster的启动命令。
```
// Setup the command to run the AM
    List<String> vargs = new ArrayList<String>(8);
    vargs.add(MRApps.crossPlatformifyMREnv(jobConf, Environment.JAVA_HOME)
        + "/bin/java");

    // TODO: why do we use 'conf' some places and 'jobConf' others?
    long logSize = jobConf.getLong(MRJobConfig.MR_AM_LOG_KB,
        MRJobConfig.DEFAULT_MR_AM_LOG_KB) << 10;
    String logLevel = jobConf.get(
        MRJobConfig.MR_AM_LOG_LEVEL, MRJobConfig.DEFAULT_MR_AM_LOG_LEVEL);
    int numBackups = jobConf.getInt(MRJobConfig.MR_AM_LOG_BACKUPS,
        MRJobConfig.DEFAULT_MR_AM_LOG_BACKUPS);
    MRApps.addLog4jSystemProperties(logLevel, logSize, numBackups, vargs, conf);

    // Check for Java Lib Path usage in MAP and REDUCE configs
    warnForJavaLibPath(conf.get(MRJobConfig.MAP_JAVA_OPTS,""), "map", 
        MRJobConfig.MAP_JAVA_OPTS, MRJobConfig.MAP_ENV);
    warnForJavaLibPath(conf.get(MRJobConfig.MAPRED_MAP_ADMIN_JAVA_OPTS,""), "map", 
        MRJobConfig.MAPRED_MAP_ADMIN_JAVA_OPTS, MRJobConfig.MAPRED_ADMIN_USER_ENV);
    warnForJavaLibPath(conf.get(MRJobConfig.REDUCE_JAVA_OPTS,""), "reduce", 
        MRJobConfig.REDUCE_JAVA_OPTS, MRJobConfig.REDUCE_ENV);
    warnForJavaLibPath(conf.get(MRJobConfig.MAPRED_REDUCE_ADMIN_JAVA_OPTS,""), "reduce", 
        MRJobConfig.MAPRED_REDUCE_ADMIN_JAVA_OPTS, MRJobConfig.MAPRED_ADMIN_USER_ENV);

    // Add AM admin command opts before user command opts
    // so that it can be overridden by user
    String mrAppMasterAdminOptions = conf.get(MRJobConfig.MR_AM_ADMIN_COMMAND_OPTS,
        MRJobConfig.DEFAULT_MR_AM_ADMIN_COMMAND_OPTS);
    warnForJavaLibPath(mrAppMasterAdminOptions, "app master", 
        MRJobConfig.MR_AM_ADMIN_COMMAND_OPTS, MRJobConfig.MR_AM_ADMIN_USER_ENV);
    vargs.add(mrAppMasterAdminOptions);
    
    // Add AM user command opts
    String mrAppMasterUserOptions = conf.get(MRJobConfig.MR_AM_COMMAND_OPTS,
        MRJobConfig.DEFAULT_MR_AM_COMMAND_OPTS);
    warnForJavaLibPath(mrAppMasterUserOptions, "app master", 
        MRJobConfig.MR_AM_COMMAND_OPTS, MRJobConfig.MR_AM_ENV);
    vargs.add(mrAppMasterUserOptions);

    if (jobConf.getBoolean(MRJobConfig.MR_AM_PROFILE,
        MRJobConfig.DEFAULT_MR_AM_PROFILE)) {
      final String profileParams = jobConf.get(MRJobConfig.MR_AM_PROFILE_PARAMS,
          MRJobConfig.DEFAULT_TASK_PROFILE_PARAMS);
      if (profileParams != null) {
        vargs.add(String.format(profileParams,
            ApplicationConstants.LOG_DIR_EXPANSION_VAR + Path.SEPARATOR
                + TaskLog.LogName.PROFILE));
      }
    }

    vargs.add(MRJobConfig.APPLICATION_MASTER_CLASS);
    vargs.add("1>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR +
        Path.SEPARATOR + ApplicationConstants.STDOUT);
    vargs.add("2>" + ApplicationConstants.LOG_DIR_EXPANSION_VAR +
        Path.SEPARATOR + ApplicationConstants.STDERR);


    Vector<String> vargsFinal = new Vector<String>(8);
    // Final command
    StringBuilder mergedCommand = new StringBuilder();
    for (CharSequence str : vargs) {
      mergedCommand.append(str).append(" ");
    }
    vargsFinal.add(mergedCommand.toString());

    LOG.debug("Command to launch container for ApplicationMaster is : "
        + mergedCommand);
```
这么冗长繁琐， 做的事情很简单。 就是在拼接启动java进程的命令。简化版本就是：
`/bin/java org.apache.hadoop.mapreduce.v2.app.MRAppMaster`

那么问题来了， 同理， MRAppMaster启动MapTask或者ReduceTask， 命令是啥呢？
``` MapReduceChildJVM.getVMCommand(
        taskAttemptListener.getAddress(), remoteTask, jvmID)```
MapReduce中， MapTask或ReduceTask的进程入口就是`YarnChild`

到这里， 遗留很久的两个问题：
2. RMAppMaster是如何启动MapTask和ReduceTask？
3. MapTask和ReduceTask的协作方式？
就有一个粗略的答案了。

2. RMAppMaster是如何启动MapTask和ReduceTask？
  经过JobImpl --> TaskImpl --> TaskAttemptImpl3个状态机的各种折腾(init 和 start两步)。 
  最后启动以YarnChild类为入口的进程。

3. MapTask和ReduceTask的协作方式？
 
  非Local, 非Uber模式的任务。 MapTask完成了一定的比例后， 开始启动ReduceTask. 进程间的通信方式为HDFS文件。
  

当然， 这样的回答太粗略了。 接下来就是细化， 丰富这一答案。