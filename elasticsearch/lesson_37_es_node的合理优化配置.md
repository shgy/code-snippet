1. 所谓集群就是Node的集合。
es的Node有4种角色：Master-eligible, Data, Client, Tribe， 这里主要说前面3种，因为Tribe是特殊的Client。
其实到最新的版本，ES又添加了一种`Ingest Node`，这里先略过。

3种角色通过如下的两个配置参数控制：

`node.master=true`, 该Node就是一个具有`Master-eligible`功能的节点
`node.data=true`, 该Node就是一个具有存储数据，和操作数据功能的`Data`节点
`node.master=false,node.data=false`, 该节点就是类似智能路由器的节点了， 不存储数据，不参与集群管理。只是用于分发客户端请求。

这两个参数配合，有4中情况：
```
0) node.master:true,  node.data:true   默认，该Node同时可以承担3种角色，类似于小公司的CTO, 既是开发，又是管理，还得跟客户沟通商务。
1) node.master:false，node.data:true， 该Node不参与选举，放弃了管理路线，往技术专家方向上走。存储数据，并处理数据相关的请求。
2) node.master:true， node.data:false，该Node不存储数据，管理控制集群。
3) node.master:false，node.data:false，该节点就变成了一个负载均衡器。
```

对于小集群， 默认的配置是没有问题的。但是如果集群变大，管理方式就该有变化了。加入集群有上百个Node,
没有必要每个节点都是五项全能(master-文能提笔安天下, data-武能跃马定乾坤)，选取小部分节点(3个)作为管理者
```
node.master:true
node.data:false
```

选取一部分作为中层管理`coordinating node`，只做请求的分发。

```
node.master=false
node.data=false
```

剩下的大部分作为员工

```
node.master:false
node.data:true
```

那么问题来了， 如可控制节点不接受客户端请求呢？








参考：
https://zhaoyanblog.com/archives/319.html
https://www.elastic.co/guide/en/elasticsearch/reference/2.1/modules-node.html
