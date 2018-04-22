系统环境：ubuntu-14.04L  /  oracle-jdk-1.8.0_51  
初步学习，安装单机版。

第一步：下载并安装hbase到ubuntu
```
1 wget http://archive.apache.org/dist/hbase/hbase-0.95.0/hbase-0.95.0-hadoop2-bin.tar.gz
2 tar -zxvf hbase-0.95.0-hadoop2-bin.tar.gz
3 mkdir /opt/hbase-0.95.0
4 sudo mv hbase-0.95.0-hadoop2-bin/* /opt/hbase-0.95.0
```
第二步：配置/etc/hosts（由于ubuntu将主机的回环路径设置成了127.0.1.1，这在hbase中会引起一些错误）
```
127.0.0.1	localhost
#127.0.1.1	shgy-VirtualBox
127.0.0.1	shgy-VirtualBox
127.0.0.1	ubuntu.shgy-VirtualBox ubuntu
```
（这是从hbase-0.95.0的docs中看到的，所以一定要仔细看文档。昨天折腾了好久，一直没有成功，问题就在这里！！）
（注：使用hbase-1.0.0版本，不用修改hosts）

第三步：配置hbase的conf/hbase-env.sh  和 conf/hbase-site.xml 文件
# =============  conf/hbase-site.xml ================
```
<configuration>
    <property>
        <name>hbase.rootdir</name>
        <value>file:///home/shgy/hbase</value>
     </property>
 
    <property>
        <name>hbase.zookeeper.property.dataDir</name>
        <value>/home/shgy/zookeeper</value>
     </property>
</configuration>
```
hbase-env.sh需要配置如下的几个字段：
```
 # The java implementation to use.  Java 1.6 required.
 29  export JAVA_HOME=$JAVA_HOME
110 # Tell HBase whether it should manage it's own instance of Zookeeper or not.
111  export HBASE_MANAGES_ZK=true
```
一切准备工作就绪后，就可以启动hbase了
bin/start-hbase.sh即可启动hbase
bin/stop-hbase.sh即可停止hbase

bin/hbase shell 即可启动hbase的客户端，对hbase进行操作。


在hbase 1.0.0中，访问http://127.0.0.1:34307/master-status?filter=all

即可看到hbase的web监控界面。







