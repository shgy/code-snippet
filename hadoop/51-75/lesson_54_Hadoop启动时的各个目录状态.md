一个大的Hadoop集群, 难免会遇到各种问题. 比如NameNode的机器宕机, DataNode的机器宕机等. 机器故障排除后, Namenode, Datanode需要重启,
恢复集群的正常运行. 出问题时, 集群指定的Namenode/Datanode的目录有可能处于各种状态. 只有熟知各种状态对Namenode/Datanode启动的影响.
在修复问题时才能够采取正确的措施.
`TestDFSStorageStateRecovery`