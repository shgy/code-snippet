机器已经安装hbase 伪集群，solr cloud。

https://github.com/NGDATA/hbase-indexer/wiki/Tutorial



1 启动solrcloud

```

java -Dbootstrap_confdir=./solr/collection1/conf -Dcollection.configName=myconf -DzkHost=localhost:2181/solr -DnumShards=2 -jar start.jar

java -Djetty.port=7574 -DzkHost=localhost:2181/solr -jar start.jar

```



2 启动hbase

```

start-dfs.sh 

start-yarn.sh

start-hbase.sh

```

3 启动lily


