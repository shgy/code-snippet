在启动MiniDFSCluster时, 会报
```
Unable to load native-hadoop library for your platform... using builtin-java classes where applicable
```
在vm arguments中添加
```
-Djava.library.path=/opt/hadoop-2.6.0/lib:/opt/hadoop-2.6.0/lib/native
```
即可
```
serverConf.setInt("dfs.namenode.fs-limits.min-block-size", 0);
```