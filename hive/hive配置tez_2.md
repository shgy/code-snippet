以前， 弄过一次tez的配置，但是感觉不咋优雅。今天， 由于需求， 重新弄了一次，过程记录一下。

```cat ~/.bashrc
export JAVA_HOME=/opt/jdk1.7.0_80
export CLASSPATH=.:${JAVA_HOME}/lib/dt.jar:${JAVA_HOME}/lib/tools.jar
export PATH=${JAVA_HOME}/bin:$PATH

export HADOOP_HOME=/opt/hadoop-2.6.0
export PATH=${HADOOP_HOME}/bin:$PATH
export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native
export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib"


export HIVE_HOME=/opt/hive-1.1.1
export PATH=${HIVE_HOME}/bin:$PATH

#
#export HIVE_OPTS='--hiveconf mapred.job.tracker=local 
#                         --hiveconf fs.default.name=file:///opt/hive-1.1.1/local-fs
#                         --hiveconf hive.metastore.warehouse.dir=file:///opt/hive-1.1.1/warehouse 
#                         --hiveconf  hive.root.logger=DEBUG,console'
#

export TEZ_HOME=/opt/tez-0.7.0
export TEZ_CONF_DIR=${TEZ_HOME}/conf

```

1. 安装hadoop+yarn单机伪分布式
``` cat core-site.xml
<configuration>
  <property>
    <name>fs.defaultFS</name>
    <value>hdfs://localhost:9000</value>
  </property>
  <property>
       <name>hadoop.tmp.dir</name>
       <value>file:///opt/hadoop-2.6.0/data-dir/tmp</value>
       <description>Abase for other temporary directories.</description>
</property>
</configuration>
```

``` cat hdfs-site.xml
<configuration>
  <property>
     <name>dfs.replication</name>
     <value>1</value>
  </property>
  
  <property>
        <name>dfs.namenode.name.dir</name>
        <value>file:///opt/hadoop-2.6.0/data-dir/dfs/name</value>
    </property>
    <property>
        <name>dfs.datanode.data.dir</name>
        <value>file:///opt/hadoop-2.6.0/data-dir/dfs/data</value>
    </property>
</configuration>
```

``` cat yarn-site.xml
<configuration>

<!-- Site specific YARN configuration properties -->
  <property>
    <name>yarn.nodemanager.aux-services</name>
    <value>mapreduce_shuffle</value>
  </property>
</configuration>
```

```cat mapred-site.xml
<configuration>
<property>
     <name>mapreduce.framework.name</name>
     <value>yarn</value>
 </property>
</configuration>
```

测试：
```
hadoop jar share/hadoop/mapreduce/hadoop-mapreduce-examples-2.6.0.jar grep /user/hadoop/input /user/hadoop/output 'dfs[a-z.]+'
```

2. 安装hive + mysql
```
sudo apt-get install mysql-server-5.6
sudo apt-get install libmysql-java
ln -s /usr/share/java/mysql-connector-java.jar $HIVE_HOME/lib/mysql-connector-java.jar


mysql> CREATE DATABASE metastore;
 
mysql> USE metastore;
 
mysql> SOURCE $HIVE_HOME/scripts/metastore/upgrade/mysql/hive-schema-0.14.0.mysql.sql;


mysql> CREATE USER 'hiveuser'@'%' IDENTIFIED BY 'hivepassword'; 
 
mysql> GRANT all on *.* to 'hiveuser'@localhost identified by 'hivepassword';
 
mysql>  flush privileges;
```

``` cat hive-site.xml
<configuration>
   <property>
      <name>javax.jdo.option.ConnectionURL</name>
      <value>jdbc:mysql://localhost/metastore?createDatabaseIfNotExist=true</value>
      <description>metadata is stored in a MySQL server</description>
   </property>
   <property>
      <name>javax.jdo.option.ConnectionDriverName</name>
      <value>com.mysql.jdbc.Driver</value>
      <description>MySQL JDBC driver class</description>
   </property>
   <property>
      <name>javax.jdo.option.ConnectionUserName</name>
      <value>hive</value>
      <description>user name for connecting to mysql server</description>
   </property>
   <property>
      <name>javax.jdo.option.ConnectionPassword</name>
      <value>hive123</value>
      <description>password for connecting to mysql server</description>
   </property>
   <property>
    <name>hive.server2.authentication</name>
    <value>NONE</value>
</property>
  <property>
    <name>hive.execution.engine</name>
    <value>mr</value>
</property>
</configuration>
```

3. 安装 tez
验证是否安装成功，
```
git clone https://github.com/apache/tez
cd tez
mvn clean package -DskipTests=true -Dmaven.javadoc.skip=true

hadoop dfs -mkdir -p /apps/tez
hadoop dfs -put tez-dist/target/tez-0.7.0.tar.gz /apps/tez

=== cat /opt/tez-0.7.0/conf/tez-site.xml

<?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
  <property>
   <name>tez.lib.uris</name>
   <value>hdfs://localhost:9000/apps/tez/tez-0.7.0.tar.gz</value>
  </property>
</configuration>

====  cat /opt/hadoop-2.6.0/etc/hadoop/hadoop-env.sh

export TEZ_HOME=/opt/tez-0.7.0    
for jar in `ls $TEZ_HOME |grep jar`; do
    export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$TEZ_HOME/$jar
done
for jar in `ls $TEZ_HOME/lib`; do
    export HADOOP_CLASSPATH=$HADOOP_CLASSPATH:$TEZ_HOME/lib/$jar
done

 hadoop jar /opt/tez-0.7.0/tez-examples-0.7.0.jar orderedwordcount /user/hadoop/input /user/hadoop/output
```

yarn的地址：
http://192.168.4.38:8088/cluster

参考：
http://pl.postech.ac.kr/~maidinh/blog/?p=35
