redis集群的搭建还是比较简单的。以ubuntu为例， 实践一遍。

step1: 下载redis源码, 编译
```
wget http://download.redis.io/releases/redis-3.2.11.tar.gz
tar -xf redis-3.2.11.tar.gz
cd redis-3.2.11
make

cp src/redis-server ../
cp redis-3.2.11/src/redis-cli .
cp redis-3.2.11/src/redis-trib.rb .
```

step2: 准备集群环境

```
mkdir redis-cluster && cd redis-cluster

mkdir 7000 7001 7002 7003 7004 7005

$ cat 7000/redis.conf
port 7000

daemonize yes

cluster-enabled yes

cluster-config-file nodes.conf

cluster-node-timeout 5000

appendonly yes

bind 0.0.0.0

protected-mode no

$ cp 7000/redis.conf 7001 && sed -i 's/7000/7001/g' 7001/redis.conf
$ cp 7000/redis.conf 7002 && sed -i 's/7000/7002/g' 7002/redis.conf
$ cp 7000/redis.conf 7003 && sed -i 's/7000/7003/g' 7003/redis.conf
$ cp 7000/redis.conf 7004 && sed -i 's/7000/7004/g' 7004/redis.conf
$ cp 7000/redis.conf 7005 && sed -i 's/7000/7005/g' 7005/redis.conf
```

step3: 安装集群依赖的ruby工具
```
sudo apt-get install ruby rubygems -y
wget https://rubygems.org/downloads/redis-3.2.1.gem
sudo gem install -l redis-3.2.1.gem 
```

step4: 启动redis的各个节点
```
$ cat start-nodes.sh 
#!/bin/bash
cd 7000 && ../redis-server redis.conf && cd ..
cd 7001 && ../redis-server redis.conf && cd ..
cd 7002 && ../redis-server redis.conf && cd ..
cd 7003 && ../redis-server redis.conf && cd ..
cd 7004 && ../redis-server redis.conf && cd ..
cd 7005 && ../redis-server redis.conf && cd ..

$ sh start-nodes.sh
./redis-trib.rb create --replicas 1 127.0.0.1:7000 127.0.0.1:7001 127.0.0.1:7002 127.0.0.1:7003 127.0.0.1:7004 127.0.0.1:7005

```

step5: 检测是否启动成功
```
./redis-cli -c -p 7000
127.0.0.1:7000> cluster nodes
47d41893f763185468eb2698c02528c23e4add9a 127.0.0.1:7002 master - 0 1527951025273 3 connected 10923-16383
34b15fcd6cf627724b1bf0fe0d536c3bc4459037 127.0.0.1:7001 master - 0 1527951025773 2 connected 5461-10922
d9d0fb8b7ab80c0acfb063a5d56a9b92df862326 127.0.0.1:7005 slave 47d41893f763185468eb2698c02528c23e4add9a 0 1527951024772 6 connected
0bd5d075e07f4931ca491e05210c8d6d6029384c 127.0.0.1:7000 myself,master - 0 0 1 connected 0-5460
8c7765667414826f1ac64b46ed76073882e4f50c 127.0.0.1:7003 slave 0bd5d075e07f4931ca491e05210c8d6d6029384c 0 1527951026775 4 connected
3bac5e536dc2f0f6ea62756daa66c00757f968fd 127.0.0.1:7004 slave 34b15fcd6cf627724b1bf0fe0d536c3bc4459037 0 1527951026775 5 connected
```

如果需要重建集群，kill各个nodes， 然后删除除了redis.conf外的文件重启节点即可。

参考:
http://www.cnblogs.com/gomysql/p/4395504.html
