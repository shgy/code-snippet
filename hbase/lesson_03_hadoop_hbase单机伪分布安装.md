1 bashrc配置

```shell

# config hadoop

export HADOOP_HOME=/opt/hadoop-2.6.0

export PATH=$PATH:$HADOOP_HOME/bin

export PATH=$PATH:$HADOOP_HOME/sbin

export HADOOP_MAPRED_HOME=$HADOOP_HOME

export HADOOP_COMMON_HOME=$HADOOP_HOME

export YARN_HOME=$HADOOP_HOME

export HADOOP_COMMON_LIB_NATIVE_DIR=$HADOOP_HOME/lib/native

export HADOOP_OPTS="-Djava.library.path=$HADOOP_HOME/lib"

# config hadoop over



# config hbase

export HBASE_HOME=/opt/hbase-1.0.0

export PATH=$PATH:$HBASE_HOME/bin

```

2  core-site.xml配置

```xml

<configuration>

    <property>

        <name>hadoop.tmp.dir</name>

        <value>/opt/hadoop-2.6.0/tmp</value>

        <description>Abase for other temporary directories.</description>

    </property>

    <property>

        <name>fs.defaultFS</name>

        <value>hdfs://localhost:9000</value>

    </property>

</configuration>

```

3 修改mapred-site.xml.template 为mapred-site.xml

```xml

<configuration>

 <property>

     <name>mapreduce.framework.name</name>

	 <value>yarn</value>

 </property>

</configuration>

```



4 yarn-site.xml

```xml



<property>

<name>yarn.nodemanager.aux-services</name>

<value>mapreduce_shuffle</value>

</property>

</configuration>

```

5  修改hdfs-site.xml

```xml


<configuration>

    <property>

        <name>dfs.replication</name>

        <value>1</value>

    </property>

    <property>

        <name>dfs.namenode.name.dir</name>

        <value>file:/opt/hadoop-2.6.0/tmp/dfs/name</value>

    </property>

    <property>

       <name>dfs.datanode.data.dir</name>

       <value>file:/opt/hadoop-2.6.0/tmp/dfs/data</value>

   </property>

   <property>

     <name>dfs.permissions</name>

	 <value>false</value>

   </property>

</configuration>

```



6 启动：

```

start-dfs.sh

start-yarn.sh

```



7 配置hbase

```xml


<configuration>

 <property>

   <name>hbase.rootdir</name>

   <value>hdfs://localhost:9000/hbase</value>

  </property>



 <property>

   <name>hbase.cluster.distributed</name>

   <value>true</value>

  </property>

  <property>

     <name>hbase.zookeeper.quorum</name>

      <value>localhost</value>

  </property>

</configuration>



```

8 启动hbase

`start-hbase.sh`

`hbase shell`










