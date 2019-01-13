ES作为一个分布式系统，需要多个节点协同，来管理集群，处理用户请求。那么很自然有个问题，ES的集群是如何组建起来的？

所谓集群，就是多台计算机一起协同工作。 既然是协同工作，那么就必须步调一致，步调一致才能得解放。需要有领导这个角色来协调资源, 这个角色在ES中命名为Master。 Master这个角色，不是ES的独有的，基本上所有的分布式系统都有这个角色的存在，比如Zookeeper, Mongo等。 

Master的产生机制也很有意思: 选举。既然是选举，那么必然会出现一个问题:怎么选举？ 这就是所谓的“选举算法”。 “选举算法”中最有名的就是Paxos算法，也是最难理解的算法。好在ES用的不是这么复杂的算法，ES用的是Bully算法。ES需要解决的问题是节点的选举， 而Paxos算法除了选举，还解决了一致性的问题，杀鸡焉用牛刀。

基于Bully算法，ES实现ZenDiscovery。 顺便说一句，ES的节点发现由统一的模块处理，就是`DiscoveryModule`。有兴趣了解ES源码，可以作为一个入口。

ZenDiscovery的流程相当简洁， 就两步:
```
1. 每个节点和其他的节点通信，获取其他节点的nodeId, 从中选取nodeId最小的那个作为自己的投票。
2. 每个节点接收其他节点的投票，如果有一个节点得到足够多的选票，则接受自己成为Leader的事实，开始分发节点状态到整个集群的其他节点。
```

下面通过具体的代码来理解这一个流程。既然是理解ES集群的组建过程，那么就从ES的进程启动开始，以elasticsearch-2.4.5为例。

我们知道，ES的启动命令是`bin/elasticsearch`,这个命令会调用`org.elasticsearch.bootstrap.Elasticsearch.java`的main方法。 
启动elasticsearch后， 通过使用如下的命令可以确定:
```
$ jps
13201 Elasticsearch

$$ cat /proc/13201/cmdline | strings
/opt/jdk1.8.0_51/bin/java
-Xms256m
-Xmx1g
-Djava.awt.headless=true
-XX:+UseParNewGC
-XX:+UseConcMarkSweepGC
-XX:CMSInitiatingOccupancyFraction=75
-XX:+UseCMSInitiatingOccupancyOnly
-XX:+HeapDumpOnOutOfMemoryError
-XX:+DisableExplicitGC
-Dfile.encoding=UTF-8
-Djna.nosys=true
-Des.path.home=/opt/elasticsearch-2.4.5-SNAPSHOT
/opt/elasticsearch-2.4.5-SNAPSHOT/lib/elasticsearch-2.4.5-SNAPSHOT.jar:/opt/elasticsearch-2.4.5-SNAPSHOT/lib/*
org.elasticsearch.bootstrap.Elasticsearch
start
```

接下来，会实例化一个Node对象，代表这个ES节点。然后start这个node.
```
 Bootstrap.init(args); // Elasticsearch.java line 45

 INSTANCE.start();   // Bootstrap.java  line 288

 node.start();      // Bootstrap.java  line 222

 discoService.joinClusterAndWaitForInitialState();  // Node.java 286

```

如果在es的配置文件进行如下的配置，那么可以debug这个过程.
```
// 当前启动节点的IP地址
network.host: 192.168.43.239 

// 集群的IP列表
discovery.zen.ping.unicast.hosts: ["192.168.43.239", "192.168.43.239:9800","192.168.43.239:9900"]
```

我们忽略代码间的跳转，直接到核心业务逻辑代码`ZenDiscovery.innerJoinCluster()`，具体的业务逻辑如下:

s1: 确定master `ZenDiscovery.findMaster()`
s2: 判断master是否是当前节点，如果是则等待其他的节点加入；否则连接master, 然后发起状态更新的请求到master.

```

关于集群节点间的通信，还有很多其他的细节。我们先抛开ES相关的知识，回到操作系统层面。 两台计算机通信，依赖的是计算机网络方面的知识。 简单来说就是TCP/IP协议。从Java语言的实现来说就是Socket编程。 Socket编程遵循的模式是C/S模式，即一台计算机作为服务端，监听一个端口；另一台计算机作为客户端，连接该端口。

通常开发中不会自己使用原生的Socket编程，而是使用Netty框架。 Netty框架封装了繁杂的底层操作，又在性能上做了很多工作。其基于异步/时间驱动的特性使其成为网络编程的首选框架。

基于Netty框架， ES构建了功能上类似于dubbo的RPC服务，这个就是Transport。代码的入口是`TransportModule`。关于Transport的细节，需单独写博客说明，非本文关注的重点，略过。


基于Transport模块，ES构建了DiscoveryModule，就是ES的节点发现。即本文试图理解的核心点。 


参考:
https://www.jianshu.com/p/9454ac19921d

https://www.elastic.co/blog/found-leader-election-in-general









