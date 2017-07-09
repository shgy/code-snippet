ApplicationClientProtocol 在ResourceManager端的实现类为： ClientRMService . 
用于Client和ResourceManager的交互。

`getNewApplication`

  相当于吃饭点菜时的时先领个牌牌， 有了这个牌牌后， 再拿着牌牌领取饭菜。
  applicationCounter, 原子自增
  application_${timestamp}_${applicationCounter}

`submitApplication`

   会在rmContext.applications(RMContextImpl类) 中登记RMAppImpl
   然后向相关组件发送RMAppEventType.START事件, RMStateStore 保存RMAppImpl信息。

`forceKillApplication`

   通过applicationId从rmContext中获取RMAppImpl信息。
   发送RMAppEventType.KILL事件到相关的组件

`getApplicationAttempts`

   通过applicationId从rmContext中获取RMAppImpl信息。
   检测权限
   返回RMAppImpl记录的appAttemps信息。

`getApplicationAttemptReport`
   
   与`getApplicationAttempts`类似，不赘述

`getApplicationReport`
   
   返回应用状态.
      通过applicationId从rmContext中获取RMAppImpl信息。
      检测权限
      返回RMAppImpl记录的currentAttempt信息。

`getApplications`
   指定过滤器后，返回的应用。
   
`getClusterMetrics`
   目前就只返回NodeManager的个数
   
`getClusterNodes`
  
   集群NodeManager的相关信息
   ```
       NodeReport report =
           BuilderUtils.newNodeReport(rmNode.getNodeID(), rmNode.getState(),
               rmNode.getHttpAddress(), rmNode.getRackName(), used,
               rmNode.getTotalCapability(), numContainers,
               rmNode.getHealthReport(), rmNode.getLastHealthReportTime(),
               rmNode.getNodeLabels());
   ```

` getContainerReport`
   通过containerId得到container的信息
   Container的信息存储在rmContext.getScheduler() , 即调度器中。
   
   
` getContainers`

   通过appAttempId得到调度器中存活的container信息
   ```
   SchedulerAppReport schedulerAppReport =
             this.rmContext.getScheduler().getSchedulerAppInfo(appAttemptId);
         if (schedulerAppReport != null) {
           rmContainers = schedulerAppReport.getLiveContainers();
         }
   ```
   
`getDelegationToken`
   获取通信的授权令牌

`getQueueInfo`
  粗浅地学习了一下Linux的进程调度， 调度的进程就是用队列这种数据结构存储的。
  
` moveApplicationAcrossQueues`
  没搞懂这个功能有啥用， 先略过

`submitReservation/deleteReservation/updateReservation`

ReservationSystem后，也支持"资源预留"，ReservationSystem是一个组件，
它允许用户指定在一段时间内使用资源的大致情况和时间约束(例如截止时间),允许预留资源来确保重要jobs的预期执行。
ReservationSystem在整个过程中为预留的资源跟踪资源，控制许可，动态地命令scheduler来确保预留被实现。