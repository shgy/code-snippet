阶段一:编译hive源码到eclipse中
参考: https://cwiki.apache.org/confluence/display/Hive/HiveDeveloperFAQ#HiveDeveloperFAQ-HowdoIimportintoEclipse?
```
git clone https://github.com/apache/hive.git
git checkout release-1.1.1
mvn clean package eclipse:clean eclipse:eclipse  -Phadoop-2 -Pitests -DskipTests -DdownloadSources -DdownloadJavadocs
```
编译需要很长时间,主要是下载Java的各种jar包, 编译成功后,直接import到eclipse中即可看到源码.

阶段二:在eclipse中调试Hive.
参考: http://datavalley.github.io/2015/10/16/Hive%E6%BA%90%E7%A0%81%E8%A7%A3%E6%9E%90%E4%B9%8B%E6%9C%AC%E5%9C%B0%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA
step 1: 编译Hive
```
mvn clean package -DskipTests -Phadoop-2 -Pdist
```
这一步会很快,这是因为在阶段一已经准备好了相关的jar包.

step 2: 以调试的方式启动hive
```
hive/packaging/target/apache-hive-1.1.1-bin/apache-hive-1.1.1-bin$ ./bin/hive --debug --hiveconf hive.root.logger=DEBUG,console
Listening for transport dt_socket at address: 8000
```

step 3: 在eclipse中选择[hive-cli] -> [debug-as] -> [Debug Configurations] -> [remote-java-applications] 启动即可.
在启动前需要在CliDriver.java类的main()方法中打上断点.


------ 2016-11-05补充
需求: 如何在eclipse中直接运行Hive?

直接运行Hive, 会报异常:
```
The class "org.apache.hadoop.hive.metastore.model.MVersionTable" is not persistable.
```
搜索到解决方法为: 
```
Make a jar dependency on metastore module with hive-metastore,jar and give
higher priority to it than module source. Pretty sure there is a better way
```
简单来说, 就是在eclipse中hive-cli hive-exec中去除对hive-metastore的依赖, 然后引入编译好的hive-metastore-1.1.0.jar包. 运行即可.
运行参数为:
```
# Program arguments
--hiveconf mapred.job.tracker=local
--hiveconf fs.default.name=file:///tmp
--hiveconf hive.metastore.warehouse.dir=/home/shgy/hive_workspace/warehouse
--hiveconf  hive.root.logger=DEBUG,console
--hiveconf hadoop.bin.path=/opt/hadoop-2.6.0/bin/hadoop
--hiveconf hive.jar.path=/home/shgy/hive_workspace/hive/packaging/target/hive-1.1.1-bin/lib/hive-exec-1.1.1.jar
```
在跑mapreduce任务时, 默认会找/usr/bin/hadoop, 如果hadoop没有安装在默认位置, 则会报错. 需要设置hadoop的安装地址

需要修改hive/common/target/test-classes/hive-site.xml中的
```
<property>
  <name>javax.jdo.option.ConnectionDriverName</name>
  <value>org.apache.derby.jdbc.EmbeddedDriver</value>
  <description>Override ConfVar defined in HiveConf</description>
</property>
```

```
# VM Arguments
-Dhadoop.log.dir=/opt/hadoop-2.6.0/logs
-Dhadoop.log.file=hadoop.log
-Dhadoop.home.dir=/opt/hadoop-2.6.0
-Dhadoop.id.str=shgy
-Dhadoop.root.logger=INFO,console
-Dhadoop.policy.file=hadoop-policy.xml
-Djava.net.preferIPv4Stack=true
-Dhadoop.security.logger=INFO,NullAppender
```

还需要将Hive相关的jar包设置到CLASSPATH下.
```
# cat /opt/hive-1.1.1/bin/hive
...
for f in ${HIVE_LIB}/*.jar; do
  CLASSPATH=${CLASSPATH}:$f;
done
...
```