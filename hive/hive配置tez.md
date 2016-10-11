0 系统是Ubuntu-14.04 64位, Hive版本为1.1.1, tez版本为0.7.0.
1 安装protobuf
```
wget https://github.com/google/protobuf/releases/download/v2.5.0/protobuf-2.5.0.tar.gz
tar xzf protobuf-2.5.0.tar.gz
cd protobuf-2.5.0
sudo apt-get update
sudo apt-get install build-essential
sudo ./configure
sudo make
sudo make check
sudo make install 
sudo ldconfig
protoc --version
```

2 下载并编译tez
```
git clone https://github.com/apache/tez.git
git checkout release-0.7.0
mvn clean package -DskipTests=true -Dmavne.javadoc.skip=true
```
将tez-0.7.0目录下的jar包及tez-0.7.0/lib目录下的jar包copy到HIVE_HOME/lib下
然后进入Hive的命令行: 设置如下的参数:
```
set hive.user.install.directory=file:///tmp;
set fs.default.name=file:///;
set fs.defaultFs=file:///;
set tez.staging-dir=/tmp;
set tez.ignore.lib.uris=true;
set tez.local.mode=true;
set tez.runtime.optimize.local.fetch=true;
set hive.execution.engine=tez;
set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrict;
set hive.tez.input.format=org.apache.hadoop.hive.ql.io.CombineHiveInputFormat;
select count(1) from test;
```
关键在于设置`set hive.tez.input.format=org.apache.hadoop.hive.ql.io.CombineHiveInputFormat;`
