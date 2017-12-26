这个Bug属于配置Bug.

CapacityScheduler可配置多个队列, 以满足多变的业务需求. 比如: 公司对集群的应用有一个典型的场景:
1. 晚上12点到上午8点前, 集群会开始计算业务的报表数据, 这个时候, 为了保证业务数据能正常跑出来, 需要确保计算任务能占用绝大多数的集群资源. 
2. 上午8点到晚上11点前, 业务分析师需要处理分析的需求,跑HiveSQL, 此时报表的计算任务已经跑完,集群资源会倾斜以保证分析师能尽快获得SQL执行结果.

可以做如下的配置:
```
<property>
  <name>yarn.scheduler.capacity.root.queues</name>
  <value>hive_night,hive_daytime</value>
  <description>The queues at the this level (root is the root queue).
  </description>
</property>
<property>
  <name>yarn.scheduler.capacity.hive_night.capacity</name>
  <value>70</value>
</property>
<property>
  <name>yarn.scheduler.capacity.hive_daytime.capacity</name>
  <value>30</value>
</property>
```
在2.6.0以前, 如果配置了队列名称, 但是没有配置capacity, 就会出错.

