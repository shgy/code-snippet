elasticsearch的定位跟MySQL其实是一样的, 就是数据库. 只不过MySQL是使用SQL语言来查询数据, 而ES使用字符串匹配.
elasticsearch有各种语言的API来操作这个数据库集群, Java是其中的一种, 个人觉得Python其实更适合. 下面记录使用
Java API操作es的方法. 

ES有两种Client: Node Client和 Transport Client. 我们先看Node Client怎么用?
首先配置pom文件
```
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.sgh</groupId>
  <artifactId>esdemo</artifactId>
  <version>1.0-SNAPSHOT</version>


  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.7</maven.compiler.source>
    <maven.compiler.target>1.7</maven.compiler.target>
    <es.version>2.1.1</es.version>
  </properties>

  <dependencies>
    <dependency>
      <groupId>org.elasticsearch</groupId>
      <artifactId>elasticsearch</artifactId>
      <version>${es.version}</version>
    </dependency>
    <dependency>
      <groupId>org.elasticsearch</groupId>
      <artifactId>jna</artifactId>
      <version>4.4.0</version>
    </dependency>

    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>
  </dependencies>

</project>

``` 
Java代码
```
package com.sgh;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

import org.elasticsearch.node.NodeBuilder;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {


// on startup

        Node node = NodeBuilder.nodeBuilder().settings(Settings.builder()
                .put("path.home", "/opt/elasticsearch-2.1.1/")).node();
        Client client = node.client();

        try{

           ClusterStatsResponse resp = client.admin().cluster().clusterStats(new ClusterStatsRequest()).actionGet();
            System.out.println(resp.getStatus());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // on shutdown
            node.close();
        }

    }
}

```

启动elasticsearch  `/opt/elasticsearch-2.1.1$ ./bin/elasticsearch` 

代码执行的结果为`GREEN`, 表示集群正常.

这里要注意的是引入jna包
```
   <dependency>
      <groupId>org.elasticsearch</groupId>
      <artifactId>jna</artifactId>
      <version>4.4.0</version>
    </dependency>
```
还有就是代码里面要设置`path.home`参数. Node Client需要用到该目录存储数据, 我试了一下, `path.home`可以设置成一个空目录`"path.home", "/home/shgy/tmp/es"`. 代码运行后, 目录的结构为
```
$ tree es
es
└── data
    └── elasticsearch
        └── nodes
            └── 0
                ├── node.lock
                └── _state
                    └── global-0.st

5 directories, 2 files

```

已经简单使用了一下NodeClient, 那么NodeClient背后的机制是啥呢? 我们浅挖一下.

elasticsearch的官方文档中有记录Node类型, `https://www.elastic.co/guide/en/elasticsearch/reference/2.1/modules-node.html`
一共有4种类型. 
`Master-eligible Node` 该节点可以参加总统选举, 控制整个集群.
`Data Node` 该节点可以存储数据, 并基于数据执行CRUD操作.
`Client Node` 即不能存数据, 也没有参选资格. 只能作为 智能路由 转发 相关的操作到`master Node` 或 `data Node`.
`Tribe Node` 这个是`Client Node`的升级版, 可以跨集群工作.


使用NodeClient, 就是启动了一个新的节点. 这个是一个比较重的操作, 看日志就能看出. 但是启动TransportClient就没有这个问题. 
使用的方法如下:
```

    private static void transportClient() throws UnknownHostException {
        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", "elasticsearch").build();
        Client client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        try{
            ClusterStatsResponse resp = client.admin().cluster().clusterStats(new ClusterStatsRequest()).actionGet();
            System.out.println(resp.getStatus());


        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // on shutdown
        }

    }

```

那接下来, 有个问题就随之而来了. 这两个Client的特点分别是怎样的, 适用场景分别是怎样的?

这个问题, elasticsearch权威指南已经解答了 `https://www.elastic.co/guide/en/elasticsearch/guide/current/_transport_client_versus_node_client.html`.

由于NodeClient是作为集群的一个节点, 所以它是集群的一部分. 集群的整个状态对它是完全开放的. 因此, 从某种程度上, 它的性能会更好一些.
但是要注意应用需要部署在集群相同的局域网, 注意防火墙的问题. 而且, 如果是高并的场景, 它不太适合, 毕竟集群管理上千个Node, 也是很累的.


Transport client 相当于Rest API, 是集群上层的一个封装, 相比而言,更轻量级.  比如需要比较多的创建销毁工作的应用(桌面应用), 由于其轻量,
所以创建,销毁都对集群的影响很小.

更多内容, 可以参考`https://www.elastic.co/guide/en/elasticsearch/guide/current/index.html`






