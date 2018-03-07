dubbo通过借用spring的依赖注入和控制反转, 实现了像调用本地方法一样调用RPC. 
正好学hadoop的时候, 学习了RPC, 所以dubbo的的hello world理解起来相当简单.
由于看了耗子哥的专栏, 我觉得对于使用者来说, dubbo的监控是第一重要的. dubbo的监控是通过admin来管理的.
使用Dubbo + Tomcat + Zookeeper动手操作一下.

1. 下载dubbo-admin, 下载命令: 
```
git clone https://github.com/alibaba/dubbo.git
cd dubbo
git checkout dubbo-2.5.8
cd dubbo-admin
mvn package -Dmaven.skip.test=true
```

2. 将dubbo-admin-2.5.8.war添加到tomcat中
```
cp dubbo-admin-2.5.8.war /opt/apache-tomcat-7.0.85/webapps/
./bin/startup.sh
./bin/shutdown.sh
```

3. 下载并启动zookeeper
```
wget http://mirrors.shu.edu.cn/apache/zookeeper/zookeeper-3.3.6/zookeeper-3.3.6.tar.gz
tar -xf zookeeper-3.3.6.tar.gz -C .
cd zookeeper-3.3.6
cp conf/zoo_sample.cfg zoo.cfg
./bin/zkServer.sh start-foreground
```

4. 启动tomcat, 然后在浏览器中访问地址: `http://localhost:8090/dubbo-admin-2.5.8/`, 用户名和密码: root/root
即可看到dubbo-admin的界面了

顺便说一下: dubbo-admin使用的是velocity模板技术
