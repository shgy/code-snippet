http://hadoop.apache.org/docs/r2.6.0/hadoop-project-dist/hadoop-common/releasenotes.html
https://issues.apache.org/jira/browse/YARN-2819

先了解两个概念:

1. 什么是leveldb ? 
LevelDB是google公司开发出来的一款超高性能kv存储引擎,以其惊人的读性能和更加惊人的写性能在轻量级nosql数据库中鹤立鸡群.
参考: http://yijiebuyi.com/blog/22f8473b2aab34a649670ad0bcbaffc3.html

2. 什么是timeline?

在lesson_21_Yarn_ApplicationHistoryServer中说过一下.

之前运行在YARN上的计算框架中，只有MapReduce配有Job History server，该server可以供用户查询已经运行完成的作业的信息，
随着YARN上计算框架的增多，有必要增加一个通用的Job History Server，于是开发了Generic history server，后来改名为
Application Timeline Server，相关文档说明见：Application Timeline Server。注：Application Timeline Server
可认为YARN提供给应用程序的用于共享信息的共享存储模块，可以将metric等信息存到该模块中，不仅仅是历史作业运行信息。
目前共享存储模块使用的是单机版的leveldb，用户可根据需要扩展成hbase等。
参考: http://dongxicheng.org/mapreduce-nextgen/hadoop-2-4-0-new-features/


Yarn的Timeline Server有两大职责:
一. 处于已完成状态的应用的通常信息
比如:
ApplicationSubmissionContext中存储的用户信息, applicationId 等应用级别(application level)的信息
每一次application-attempt的信息
每一个Container的信息
二. 每个正在运行或者已经完成的应用所属框架(Mapreduce/Tez,Spark)的信息
比如:
MapReduce框架的map任务数, reduce任务数.


hadoop跟leveldb的交互方式: jni, 参见 `https://github.com/fusesource/leveldbjni`

TimelineServer中记录着集群运行的所有信息: app/attempt/container/...
```
$ yarn application -status <Application ID>
$ yarn applicationattempt -list <Application ID>
$ yarn applicationattempt -status <Application Attempt ID>
$ yarn container -list <Application Attempt ID>
$ yarn container -status <Container ID>
```

感觉拿到了leveldb文件, 就拿到了集群运行的历史. 


bug原因: 

``` 
The NPE happens because the data integrity assumes that no entity has a null domainId. 
However, if leveldb already contains the timeline data that are generated 
by prior 2.6 timeline server, the integrity is broken. Previously,
 the entity doesn't have the domain information. 
Will work on a fix to be compatible to the existing store.
```
2.6版本的entity的完整性假设: 所有的entity都有domainId
如果leveldb中存在2.6以前生成的集群运行信息, 这个完整性假设就不成立了. 因为2.6之前, entity压根没有domainId这个字段.
需要做一个向下兼容. 
所以, 这个bug会在集群升级的时候爆出来..


参考: http://hadoop.apache.org/docs/r2.7.3/hadoop-yarn/hadoop-yarn-site/TimelineServer.html