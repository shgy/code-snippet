1. NodeManager定期汇报节点信息给ResourceManager. 

2. ResourceManager应答心跳， 给NodeManager下达指令: 释放Container

3. ResourceManager收到NodeManager的心跳信息， 会触发Node_update事件。

4. ResourceScheduler收到Node_update事件后， 会按照一定的策略将NodeManager的资源
   分配到各个应用程序， 将分配结果存放到ResourceManager的一个数据结构中.

5. ApplicationMaster向ResourceManager发送周期性的心跳， 领取新分配的Container

6. ResourceManager通过心跳将container信息返回给ApplicationMaster

7. ApplicationMaster再执行内部分配。

可见： 调度的核心在第4步。 可参考CapacityScheduler.handle()方法。