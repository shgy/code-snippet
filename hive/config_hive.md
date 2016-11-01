使用Hive的过程中, 遇到了如下的几个问题:
1 由于hive配置的默认执行引擎`hive.execution.engine`是mr, 而它速度太慢了, 通常习惯使用tez. 每次启动Hue都需要先执行
```
set hive.execution.engine=tez;
```
2 自己搭建了一个单机版的CDH, 使用Hive时, 默认的模式是分布式模式. 这样在单机伪分布环境中执行会比较慢,或者根本执行不下去,因此需要设置
```
set hive.exec.mode.local.auto=true;
```

这迫使我思考一个问题: Hive设置变量还有哪些方法? 怎么让启动的Hive默认值就是自己希望的?
感谢万能的Google, 
参考文档(官方): https://cwiki.apache.org/confluence/display/Hive/AdminManual+Configuration#AdminManualConfiguration-ConfiguringHive

一共有4种方法:
1. set命令
2. --hiveconf
3. hive-site.xml
4. hivemetastore-site.xml/hiveserver2-site.xml

变量生效的顺序如下:
hive-site.xml -> hivemetastore-site.xml -> hiveserver2-site.xml --> `-hiveconf`命令行参数

