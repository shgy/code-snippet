hadoop当前不支持ipv6, 因此如果系统中安装hadoop,则需要禁止ipv6.
在ubuntu中操作方法如下:
```
vim /etc/sysctl.conf
添加
net.ipv6.conf.all.disable_ipv6 = 1
net.ipv6.conf.default.disable_ipv6 = 1
net.ipv6.conf.lo.disable_ipv6 = 1
net.ipv6.conf.eth0.disable_ipv6 = 1

sudo sysctl -p
```

如果没有关闭ipv6, 在运行测试`TestFileCreation.testFileCreationSetLocalInterface()`会报如下的错误:
```
org.apache.hadoop.ipc.RemoteException(java.io.IOException): File /user/shgy/filestatus.dat could only be replicated to 0 nodes instead of minReplication (=1).  There are 1 datanode(s) running and 1 node(s) are excluded in this operation.
	at org.apache.hadoop.hdfs.server.blockmanagement.BlockManager.chooseTarget4NewBlock(BlockManager.java:1549)
```

参考文档:
```
https://wiki.apache.org/hadoop/HadoopIPv6
```

