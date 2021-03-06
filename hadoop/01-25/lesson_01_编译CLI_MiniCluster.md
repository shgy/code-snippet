Hadoop的CLI_MiniCluster可以启动一个单结点的Hadoop集群. 这对于开发,调试基于Hadoop的应用很有帮助.参考官方文档,编译一个CLI MiniCluster.
1. 获取hadoop源码
```
git clone https://github.com/apache/hadoop.git
cd hadoop
git checkout release-2.6.0
```
2. 编译安装protobuf-2.5.0
```
./configure
make
make install
```
错误及解决方法
```
protoc: error while loading shared libraries: libprotoc.so.8: cannot open shared
```
错误原因：
protobuf的默认安装路径是/usr/local/lib，而/usr/local/lib 不在Ubuntu体系默认的 LD_LIBRARY_PATH 里，所以就找不到该lib
解决方法：
1. 创建文件 /etc/ld.so.conf.d/libprotobuf.conf 包含内容：
```
$cat /etc/ld.so.conf.d/libprotobuf.conf
/usr/local/lib
```
2. 输入命令
```
sudo ldconfig 
```
这时，再运行protoc --version 就可以正常看到版本号了

3. 使用maven编译
```
~# mvn -version
Apache Maven 3.3.9 (bb52d8502b132ec0a5a3f4c09453c07478323dc5; 2015-11-11T00:41:47+08:00)
Maven home: /opt/apache-maven-3.3.9
Java version: 1.7.0_79, vendor: Oracle Corporation
Java home: /opt/jdk1.7.0_79/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "4.2.0-27-generic", arch: "i386", family: "unix"
```

```
mvn clean install -DskipTests

```

4. 启动集群
```
export HADOOP_CLASSPATH=./share/hadoop/yarn/test/hadoop-yarn-server-tests-2.6.0-tests.jar
// 如果没有设置HADOOP_CLASSPATH, 则会报 ClassNotFoundException  MiniYARNCluster
./bin/hadoop jar ./share/hadoop/mapreduce/hadoop-mapreduce-client-jobclient-2.6.0-tests.jar  minicluster
```
