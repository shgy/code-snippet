了解ES的原理，源码是文档最好的补充，甚至比文档更有助于了解ES的内部核心。

首先从git上clone下源码:
```
git clone https://github.com/elastic/elasticsearch.git

cd elasticsearch

git tag -l

git checkout v2.4.5

sh run.sh
```
如果使用run.sh没有成功，再试一次， 有可能是maven的jar包没有下载到。

这里使用v2.4.5是由于，本来想基于公司现阶段使用的2.1.0源码，但是`https://oss.sonatype.org/content/repositories/snapshots/org/elasticsearch/rest-api-spec/`网站没有2.1.0的jar包， 所以选取了v2.4.5, 小版本其实差别不大。

这里JDK要换成1.8, 1.7的使用maven会报`protocol_verson`错误。
编译成功后，就会生成elasticsearch的zip包， 需要解压，因为源码中会用到conf文件。

```
cd /home/shgy/es_workspace/elasticsearch/distribution/integ-test-zip/target/releases/
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
