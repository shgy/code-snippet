<Hadoop技术内幕：深入解析MapReduce架构设计与实现原理>
如果用户想要让一个新的计算框架运行在YARN上, 需要将该框架重新封装成一个ApplicationMaster,
而ApplicationMaster将作为用户应用程序的一部分被提交到YARN中.

换句话说: YARN中所有计算框架实际止只是客户端的一个库.
