开发中需要用到ES的插件， 开发ES插件需要了解ES的内部结构。于是再次开始学习ES的源码。

了解ES的原理，源码是文档最好的补充。源码甚至比文档更有助于了解ES的内部核心。

首先从git上clone下源码:
```
git clone https://github.com/elastic/elasticsearch.git

cd elasticsearch

git tag -l

git checkout v2.4.5

sh run.sh
```
如果使用run.sh没有成功，再试一次， 有可能是maven的jar包没有下载到。
这里使用v2.4.5是由于在编译es的过程中会用到相关的jar包，而`https://oss.sonatype.org/content/repositories/snapshots/org/elasticsearch/rest-api-spec/`并不是所有版本的jar包都有， 所以从中选取了v2.4.5, 这跟手机选号一样，纯属个人主观。

这里JDK要换成1.8, 1.7的jdk maven会报`protocol_verson`错误。
编译成功后，就会生成elasticsearch的zip包， 需要解压，因为源码中会用到conf文件。

```
cd /home/shgy/es_workspace/elasticsearch/distribution/zip/target/releases/
unzip elasticsearch-2.4.5-SNAPSHOT.zip 
mv elasticsearch-2.4.5-SNAPSHOT /opt/
```
编译完成后， 将源码import到intellij中， intellij的启动参数
```
vm options : -Des.path.home=/opt/elasticsearch-2.4.5-SNAPSHOT
Program arguments: start
```
启动成功后使用
```
curl http://localhost:9200
```
即可看到经典的
```
{
  "name" : "Ruckus",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "ZIl7g86YRiGv8Dqz4DCoAQ",
  "version" : {
    "number" : "2.4.5",
    "build_hash" : "c849dd13904f53e63e88efc33b2ceeda0b6a1276",
    "build_timestamp" : "2018-08-12T01:30:55Z",
    "build_snapshot" : true,
    "lucene_version" : "5.5.4"
  },
  "tagline" : "You Know, for Search"
}
```

从源码启动成功后， 可以做的事情就多了。 比如看看`You Know, for Search`是怎么来的;看看ES内部的index/get/search等接口内部是如何运行的。
更重要的是， 可以将相关接口的逻辑套用， 依样画葫芦开发plugin实现自己的业务逻辑。

以debug的方式启动es后， 第一个断点可以打在`org.elasticsearch.http.netty.HttpRequestHandler.messageReceived()`，这是netty的编程模式。

比如`You Know, for Search`, 通过debug， 可以了解到其调用链为：
`HttpRequestHandler.messageReceived() --->  RestMainAction.handleRequest()`

如果开发自己的plugin, 需要提供http的访问接口， 也是这样的套路。













