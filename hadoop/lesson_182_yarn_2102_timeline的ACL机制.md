链接: https://issues.apache.org/jira/browse/YARN-2102
https://hadoop.apache.org/docs/r2.6.0/hadoop-yarn/hadoop-yarn-site/TimelineServer.html

参考: GeneralizedTimelineACLs.pdf

在2.6以前, timeline 是没有namespace这个概念的. namespace这个概念是基于这样的一个背景提出来的.

1. entities和events信息中会含有一些app相关的机密信息, 所以需要ACL. timeline当前(2.6以前)
的ACL机制如下: 
a. entity的创建者被定义为event的所有者
b. 只有owner和admins可以更新和查看entity
c. 访问授权的粒度控制在单个entity

-- 问题
2. 到目前为止: 
a. 授权不区分读写操作; 比如授权一些用户添加和修改entity, 授权另一些用户查看entity
b. 用户可能有这种需求: 将多个相关的entity组合在一起. 比如:多个job隶属于同一个workflow.
   在这种情况下, 需要阻止 攻击型job 混入到 当前的workflow中. 
   当前的entity级别的ACL, 忽视了entity的关联关系, 引入了一个安全漏洞, 攻击者可以将不相关
   的entity伪装成相关entity, 注入到用户的workspace.  ---- 这里没有弄明白怎么注入
   
文档中写到: 
```
Users can access applications' generic historic data via the command line as below. 
Note that the same commands are usable to obtain the corresponding information about running applications.

  $ yarn application -status <Application ID>
  $ yarn applicationattempt -list <Application ID>
  $ yarn applicationattempt -status <Application Attempt ID>
  $ yarn container -list <Application Attempt ID>
  $ yarn container -status <Container ID>

```
但是, 我看源码: ApplicationCLI, 压根儿没走timeline的leveldb. --- 这个问题先略过.

