使用map-reduce做数据同步的任务时， 比如：同步数据到MySQL,Redis。 如果同时启动大量的map任务， 可能会压垮服务。这时候，就需要控制并行map的数量。 
hadoop 2.7 添加了这个功能。 在Hive中测试有效。参数：  mapreduce.job.running.map.limit
