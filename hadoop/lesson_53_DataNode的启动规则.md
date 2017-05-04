Hadoop是分布式的系统, 想要NameNode和DataNode密切配合,就得处理号兼容性问题.
在系统启动时, 会做如下的检查:
1. Datanode namespaceID != Namenode namespaceID 启动失败
2. Datanode clusterID != Namenode clusterID 启动失败
3. Datanode blockPoolID != Nodenode blockPoolID 启动失败
4. 如果 softwareLV == storedLV AND DataNode.FSSCTIME == NameNode.FSSCTime, Datanode常规启动
5. 如果 Datanode启动时没有带任何参数, 并且 softwareLV > storedLV 或者 (softwareLV == storedLV AND
   DateNode.FSSCTime < NameNode.FSSCTIME), Datanode会进行升级操作.
6. 如果Datanode启动时带 -rollback选项, 并且 softwareLV >= previous.storedLV AND
DataNode.previous.FSSCTIME <= NameNode.FSSCTime, Datanode会回滚.
7. 其他剩余情况下, 启动失败.


