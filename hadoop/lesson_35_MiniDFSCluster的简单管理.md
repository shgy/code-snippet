参考来源:
```
org.apache.hadoop.test.MiniDFSClusterManager
```
使用方法如下:
```
hadoop jar /opt/hadoop-2.6.0/share/hadoop/hdfs/hadoop-hdfs-2.6.0-tests.jar
org.apache.hadoop.test.MiniDFSClusterManager -help

 -cmdport <arg>         Which port to listen on for commands (default
                        0--we choose)
 -D <property=value>    Options to pass into configuration object
 -datanodes <arg>       How many datanodes to start (default 1)
 -format                Format the DFS (default false)
 -help                  Prints option help.
 -namenode <arg>        URL of the namenode (default is either the DFS
                        cluster or a temporary dir)
 -nnport <arg>          NameNode port (default 0--we choose)
 -writeConfig <path>    Save configuration to this XML file.
 -writeDetails <path>   Write basic information to this JSON file.
```
其功能就是启动一个集群, 导出集群的配置文件.
