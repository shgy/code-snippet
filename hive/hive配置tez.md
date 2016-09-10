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


I have my HADOOP_CLASSPATH set to point to both $TEZ_HOME/* and $TEZ_HOME/lib/*. Also have TEZ_H0ME and TEZ_CONF_DIR set, though I'm not sure if these are required or not.?

set hive.user.install.directory=file:///tmp;
set fs.default.name=file:///;
set fs.defaultFS=file:///;
set tez.staging-dir=/tmp;
set tez.ignore.lib.uris=true;
set tez.runtime.optimize.local.fetch=true;
set tez.local.mode=true;
set hive.execution.engine=tez;

-- 在本地模式下没有配置成功, 先放一放