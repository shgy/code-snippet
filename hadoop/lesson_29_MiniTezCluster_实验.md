学习的整个链条是: HDFS --> MapReduce --> Yarn --> Tez  --> Hive

Hive编译SQL的部分与Hadoop无关, 属于antlr的内容.

Tez脱胎与Yarn, 所以理解了Yarn才能比较容易理解Tez.

目前已经学习了一些Yarn的基础知识, 希望了解Tez是如何运行在Hadoop集群中的.

首先, 跑通一个MiniTezCluster相关的测试样列.

具体的思路如下:
1. 创建一个tez-example项目, 目录结构如下
```
$ tree src/
src/
├── main
│   └── resources
│       └── log4j.properties
└── man
    └── java
        └── org
            └── apache
                └── tez
                    ├── mapreduce
                    │   └── examples
                    │       └── MRRSleepJob.java
                    └── test
                        ├── MiniTezCluster.java
                        └── TestMRRJobs.java

10 directories, 4 files
```
相关的Java类都是从tez-tests源码中择取出来的.
maven的pom.xml文件如下:
```
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-minicluster</artifactId>
    <version>2.6.0</version>
</dependency>
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.2</version>
</dependency>
<dependency>
    <groupId>org.apache.tez</groupId>
    <artifactId>tez-dag</artifactId>
    <version>0.8.2</version>
</dependency>
<dependency>
    <groupId>org.apache.tez</groupId>
    <artifactId>tez-mapreduce</artifactId>
    <version>0.8.2</version>
</dependency>
```
然后以junit test运行TestMRJobs.testMRRSleepJob(), 运行成功即可开始学习Tez的内部实现机制或者寻找其他更简单的tez实现.


