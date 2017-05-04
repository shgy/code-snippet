记录这个明显是挑软柿子捏. 因为, YARN内容庞杂, 即使是DistributedShell, 也远比Hello World难多了
```
    long ts = System.currentTimeMillis();
    ApplicationId a5 = ApplicationId.newInstance(ts, 45436343);
    Assert.assertEquals("application_10_0001", a1.toString());
    Assert.assertEquals("application_" + ts + "_45436343", a5.toString());
  }
```
使用yarn的Web接口, 查看任务状态,会看到如下的信息:
```
        ID                       user         Name     ApplicationType Queue     StartTime                         FinishTime                 State    FinalStatus
application_1480755187682_0004	hadoop	DistributedShell	YARN	default	Sat, 03 Dec 2016 09:11:33 GMT	Sat, 03 Dec 2016 09:11:47 GMT	FINISHED	SUCCEEDED	History
application_1480755187682_0003	hadoop	DistributedShell	YARN	default	Sat, 03 Dec 2016 09:07:25 GMT	Sat, 03 Dec 2016 09:07:39 GMT	FINISHED	SUCCEEDED	History
application_1480755187682_0002	hadoop	DistributedShell	YARN	default	Sat, 03 Dec 2016 09:05:33 GMT	Sat, 03 Dec 2016 09:05:47 GMT	FINISHED	SUCCEEDED	History
application_1480755187682_0001	hadoop	DistributedShell	YARN	default	Sat, 03 Dec 2016 08:56:51 GMT	Sat, 03 Dec 2016 08:57:07 GMT	FINISHED	SUCCEEDED	History
```
其中, 第一列在Java中, 就是`ApplicationId`对象.

```
  long timestamp = System.currentTimeMillis();
  ApplicationId appId = ApplicationId.newInstance(timestamp, 1);
  ApplicationAttemptId appAttemptId =  ApplicationAttemptId.newInstance(appId, 1);
  ApplicationReport appReport =  ApplicationReport.newInstance(
          appId,
          appAttemptId,
          "user",
          "queue",
          "appname",
          "host",
          124,
          null,
          YarnApplicationState.FINISHED,
          "diagnostics",
          "url",
          0,
          0,
          FinalApplicationStatus.SUCCEEDED,      // 任务最终状态
          null,
          "N/A",
          0.53789f,  // 任务进度
          YarnConfiguration.DEFAULT_APPLICATION_TYPE,  // 任务类型 Mapreduce 或者 DistributedShell
          null);
```

上面用到了`ApplicationAttemptId`, 它代表什么意思呢 ?  源码中有说明:
```
 * <p><code>ApplicationAttemptId</code> denotes the particular <em>attempt</em>
 * of an <code>ApplicationMaster</code> for a given {@link ApplicationId}.</p>
 *
 * <p>Multiple attempts might be needed to run an application to completion due
 * to temporal failures of the <code>ApplicationMaster</code> such as hardware
 * failures, connectivity issues etc. on the node on which it was scheduled.</p>
```

`ContainerId` 代表集群中的一个Container, 它是独一无二的.
```
  public static ContainerId newContainerId(int appId, int appAttemptId,
      long timestamp, long containerId) {
    ApplicationId applicationId = ApplicationId.newInstance(timestamp, appId);
    ApplicationAttemptId applicationAttemptId =
        ApplicationAttemptId.newInstance(applicationId, appAttemptId);
    return ContainerId.newContainerId(applicationAttemptId, containerId);
  }
```

`Resource`

`Resource`是Hadoop对集群中的计算机资源的建模, 目前只有有内存和CPU, 而且在CPU的管理上还相当粗糙.
对内存的限制很简单, 指定内存大小即可, 超过了就将任务进程杀死. 对CPU的限制, 貌似是限制同一时间可以启用的线程数.
```
  public static Resource newInstance(int memory, int vCores) {
    Resource resource = Records.newRecord(Resource.class);
    resource.setMemory(memory);
    resource.setVirtualCores(vCores);
    return resource;
  }
```




