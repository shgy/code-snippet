学习docker的基础知识。

docker的核心概念： Image/Container/Respository.
Image相当于程序， Container相当于进程。 Respository先不管。

部署hadoop集群用到的docker命令如下：

1. 从服务端pull一个ubuntu的image
```
sudo docker pull ubuntu:16.04
```

2. 启动Docker的image
```
sudo docker run  --rm -t -i -v /home/shgy/docker-hadoop-cluster:/home/shgy  ubuntu:16.04

#  --rm 参数表示 退出时就删除container
#  -v 参数表示 挂载本地目录到docker的container

sudo docker run -itd \
                -v /home/shgy/docker-hadoop-cluster:/home/shgy \
                --net=hadoop \
                --name hadoop-master \
                --hostname hadoop-master \
                ubuntu:16.04 &> /dev/null
sudo docker exec -it hadoop-master bash
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

5. 提交变更的container, 比如给ubuntu:14.04 安装vim/openjdk-7-jdk/openssh-server后保存成新的image
```
sudo apt-get install python-software-properties software-properties-common
sudo add-apt-repository ppa:openjdk-r/ppa  
sudo apt-get update   
sudo apt-get install openjdk-7-jre openssh-server
sudo docker commit -m "modify apt source list" -a "shgy" 2e312a39b4bb ubuntu-shgy
```
6. 删除image, container
```
sudo docker rmi image
sudo docker rm  container
```
