hue是hive使用者接触最多的app了。 hue的安装稍微有点麻烦。因此记录一下.
hue的版本为 hue-release-3.11.0.tar.gz。
在hue的readme.md文档中有安装过程。
```
sudo apt-get install libz-dev   python-dev \
      maven  libffi-dev  libssl-dev libkrb5-dev \
     libxml2-dev libxslt-dev libmysqlclient-dev \
     libgmp3-dev libsqlite3-dev  libz-dev \
     libssl-dev libsasl2-dev python-dev libldap2-dev libssl-dev \
     g++ tree python-pip \
     make libtidy-0.99-0 libldap2-dev libssl-dev \

make apps
make install -PREFIX=/home/vm-shgy/hue
```
除了安装前面的依赖包以外， 基本不会有问题。
然后就是配置hadoop
```
Hadoop配置文件修改

hdfs-site.xml

<property>
  <name>dfs.webhdfs.enabled</name>
  <value>true</value>
</property>
core-site.html

<property>
  <name>hadoop.proxyuser.hue.hosts</name>
  <value>*</value>
</property>
<property>
  <name>hadoop.proxyuser.hue.groups</name>
  <value>*</value>
</property>


HUE配置文件修改

[[hdfs_clusters]] [[[default]]]

fs_defaultfs=hdfs://mycluster

webhdfs_url=http://node1:50070/webhdfs/v1

hadoop_bin=/usr/hadoop-2.5.1/bin

hadoop_conf_dir=/usr/hadoop-2.5.1/etc/hadoop

启动hdfs、重启hue

访问Web
```

hue的开发文档
```
http://archive.cloudera.com/cdh5/cdh/5/hue-3.9.0-cdh5.7.5/sdk/sdk.html
```

