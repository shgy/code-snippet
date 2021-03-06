1. 编译Hue, 这会花很长时间. 一般会有各种错误. 一般Google一下都能解决.
```
sudo apt-get install libmysqlclient-dev
sudo apt-get install libsasl2-dev python-dev libldap2-dev libssl-dev
sudo git clone https://github.com/cloudera/hue.git branch-3.11.0
sudo chown -R hadoop:hadoop branch-3.11.0/
cd  branch-3.11.0/
make apps
make install
```
2. 将Hue用到的数据库配置为MySQl, 参考
http://www.cloudera.com/documentation/enterprise/latest/topics/cdh_ig_hue_database.html#cdh_ig_hue_database_mysql
该教程非常详细.

3. 启动Hive的hiveserver2
```
nohup  ./bin/hive --service hiveserver2 &
```
4. 配置Hadoop的Webhdfs
```
$cat etc/hadoop/hdfs-site.xml
...
 <property>
      <name>dfs.webhdfs.enabled</name>
      <value>true</value>
   </property>
...
```

5. 在hue中配置hdfs的地址:
```
[hadoop]

  # Configuration for HDFS NameNode
  # ------------------------------------------------------------------------
  [[hdfs_clusters]]
    # HA support by using HttpFs

    [[[default]]]
      # Enter the filesystem uri
      fs_defaultfs=hdfs://localhost:9000
```
