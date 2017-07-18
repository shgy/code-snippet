学习docker的基础知识。

docker的核心概念： Image/Container/Respository.
Image相当于程序， Container相当于进程。 Respository先不管。

部署hadoop集群用到的docker命令如下：

1. 从服务端pull一个ubuntu的image
```
sudo docker pull ubuntu:14.04
```

2. 启动Docker的image
```
sudo docker run  -rm -t -i -v /home/shgy/hadoop-cluster-docker:/home/shgy  ubuntu:14.04

#  -rm 参数表示 退出时就删除container
#  -v 参数表示 挂载本地目录到docker的container
```

3. 查看docker的image 和 docker的container
```
 sudo docker images
 sudo docker ps -a -q
```

4. 连接到后台container的shell
```
 sudo docker exec -it hadoop-master bash
```

5. 提交变更的container, 比如给ubuntu:14.04 安装vim/open-jdk/open-sshserver后保存成新的image
```
sudo docker commit -m "modify apt source list" -a "shgy" 2e312a39b4bb ubuntu-shgy
```
6. 删除image, container
```
sudo docker rmi image
sudo docker rm  container
```



部署hadoop的过程。

1. clone相关的项目 `git clone https://github.com/kiwenlau/hadoop-cluster-docker.git`
2. 编辑Dockerfile
```
FROM ubuntu-shgy:latest

MAINTAINER KiwenLau <kiwenlau@gmail.com>

WORKDIR /root

# RUN rm /etc/apt/sources.list
# ADD sources.list /etc/apt
# install openssh-server, openjdk and wget
# RUN apt-get update && apt-get -f install && apt-get install -y openssh-server openjdk-7-jdk wget

# install hadoop 2.7.2

COPY config/* /tmp/
RUN mv /tmp/hadoop-2.6.0.tar.gz /root
# RUN wget https://github.com/kiwenlau/compile-hadoop/releases/download/2.7.2/hadoop-2.7.2.tar.gz && \
RUN tar -xzvf hadoop-2.6.0.tar.gz && \
    mv hadoop-2.6.0 /usr/local/hadoop && \
    rm hadoop-2.6.0.tar.gz
```
修改的部分如下：
 将hadoop的版本替换成2.6.0， 且用本地编译好的hadoop
 基于ubuntu:14.04的image, 安装好open-jdk , 然后save成命名为ubuntu-shgy的image.

3. 编译Dockerfile
```
cd hadoop-cluster-docker
sudo sh build-image.sh
```

4. 创建桥接网络
```
sudo docker network create --driver=bridge hadoop
```
5. 启动集群
```
cd hadoop-cluster-docker
sudo ./start-container.sh
```
6. 启动集群后会进入hadoop-master的shell
start hadoop

./start-hadoop.sh
7. run wordcount

./run-wordcount.sh

注意： 由于apt默认的source.list很慢， 所以替换成了国内的源。
在这里一定要确认docker中的系统与本地系统的版本一致。
`cat /etc/issue`

由于做了端口映射， 所以在本地可以通过浏览器访问
http://localhost:8088/cluster/apps/FINISHED