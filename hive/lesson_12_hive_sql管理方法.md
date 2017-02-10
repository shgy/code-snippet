对于大数据部门的员工来说, 每天会处理大量来自运营或者其他部门的数据需求. 如何管理这些需求呢? 当然是git.
建立如下的几个目录:
1. one-off  代表一次性的任务.
   -- YY-MM
      --YYMMDD_filename

2. long-standing 代表需要配置到流程中的任务, 需要长期维护
   -- task-name
      --YYMMDD_filename

3. reusable-code 代表可复用的东西, 特别是对业务的理解文档, 常用的SQL片段
   --业务名称
     --YYMMDD_SQL片段说明

4. businesses 业务方提供的一些业务说明性的东西. 帮助理解业务. 在撕逼的时候, 也是证据.
   -- 业务资料
   -- 业务挖坑
     --YYMMDD_业务方姓名_标题

