在笔记本上安装CDH-5.6.0, 成功了. 记录整个过程. 

注意为单机安装

安装方式为离线安装, 因为在线安装下载等待太慢了.

使用的电脑为笔记本8G内存. 硬盘为SSD固态硬盘.

参考: http://blog.csdn.net/u012348345/article/details/51451455

官方安装文档：http://www.cloudera.com/documentation/enterprise/5-6-x/topics/installation.html 

Cloudera Manager地址：http://archive.cloudera.com/cm5/cm/5/ 

CDH安装包地址：http://archive.cloudera.com/cdh5/parcels/5.6.0/

###软件准备###
ubuntu-14.04.2-desktop-amd64.iso

cloudera-manager-trusty-cm5.6.0_amd64.tar.gz

CDH-5.6.0-1.cdh5.6.0.p0.45-trusty.parcel

CDH-5.6.0-1.cdh5.6.0.p0.45-trusty.parcel.sha1

manifest.json


###安装过程###
为了彻底复现安装过程, 我决定从重装系统开始安装CDH.

1.  安装操作系统: hostname命名为cdh-node-1. 安装完成后配置cdh-node-1的ip为内网IP, 比如:192.168.3.107.
```
$ vim /etc/hosts
127.0.0.1	localhost
192.168.3.107	cdh-node-1
```
2. 启用root账户
```
$ sudo passwd root
```
3.  安装ssh, 让root用户具有远程登录权限；
需要注意的在在Ubuntu-14.04下面,ssh有一个Bug.
使用如下的方式解决
```
mkdir /var/run/sshd
```
```
root@cdh-node-1:~# apt-get install openssh-server openssh-client
root@cdh-node-1:~# vim /etc/ssh/sshd_conf
# Authentication:
LoginGraceTime 120
# PermitRootLogin without-password
PermitRootLogin yes
StrictModes yes
```
4. 安装MySQL, 并且允许远程连接到MySQL.
```
root@cdh-node-1:~# apt-get install mysql-server mysql-client libmysql-java
root@cdh-node-1:~# vim /etc/mysql/my.cnf
#
# Instead of skip-networking the default is now to listen only on
# localhost which is more compatible and is not less secure.
# bind-address          = 127.0.0.1
#
# * Fine Tuning
#

```

4. 安装JDK, 并配置JAVA_HOME等环境变量
参考: http://www.cloudera.com/documentation/enterprise/5-6-x/topics/cdh_ig_jdk_installation.html#topic_29
```
root@cdh-node-1:~# mkdir /usr/java && tar -xf jdk-7u80-linux-x64.tar.gz -C /usr/java
root@cdh-node-1:~# vim /etc/profile
export JAVA_HOME=/usr/java/jdk1.7.0_80
export CLASSPATH=${JAVA_HOME}/lib/dt.jar:${JAVA_HOME}/lib/tools.jar
export PATH=${JAVA_HOME}/bin:$PATH
root@cdh-node-1:~# source /etc/profile
```
5. 安装ntp服务
```
apt-get install ntp
```
5. 开始安装CDH, 步骤基本上就是上面参考文件里面的做法了.
```
root@cdh-node-1:~# tar -xf /root/soft_cdh/cloudera-manager-trusty-cm5.6.0_amd64.tar.gz -C /opt 
root@cdh-node-1:~# mkdir -p /opt/cloudera/parcel-repo
root@cdh-node-1:~# cp soft_cdh/manifest.json /opt/cloudera/parcel-repo/
root@cdh-node-1:~# cp soft_cdh/CDH-5.6.0-1.cdh5.6.0.p0.45-trusty.parcel /opt/cloudera/parcel-repo/
root@cdh-node-1:~# cp soft_cdh/CDH-5.6.0-1.cdh5.6.0.p0.45-trusty.parcel.sha1 /opt/cloudera/parcel-repo/
root@cdh-node-1:~# cd /opt/cloudera/parcel-repo/ 
root@cdh-node-1:~# mv CDH-5.6.0-1.cdh5.6.0.p0.45-trusty.parcel.sha1 CDH-5.6.0-1.cdh5.6.0.p0.45-trusty.parcel.sha
```
由于是单机安装, 我就没有设置数据库的参数了.
```
进入mysql命令行：$ mysql -u root -p
进入mysql命令行后，直接复制下面的整段话并粘贴：
create database amon DEFAULT CHARACTER SET utf8;
grant all on amon.* TO 'amon'@'%' IDENTIFIED BY 'amon_password';
grant all on amon.* TO 'amon'@'CDH' IDENTIFIED BY 'amon_password';
create database smon DEFAULT CHARACTER SET utf8;
grant all on smon.* TO 'smon'@'%' IDENTIFIED BY 'smon_password';
grant all on smon.* TO 'smon'@'CDH' IDENTIFIED BY 'smon_password';
create database rman DEFAULT CHARACTER SET utf8;
grant all on rman.* TO 'rman'@'%' IDENTIFIED BY 'rman_password';
grant all on rman.* TO 'rman'@'CDH' IDENTIFIED BY 'rman_password';
create database hmon DEFAULT CHARACTER SET utf8;
grant all on hmon.* TO 'hmon'@'%' IDENTIFIED BY 'hmon_password';
grant all on hmon.* TO 'hmon'@'CDH' IDENTIFIED BY 'hmon_password';
create database hive DEFAULT CHARACTER SET utf8;
grant all on hive.* TO 'hive'@'%' IDENTIFIED BY 'hive_password';
grant all on hive.* TO 'hive'@'CDH' IDENTIFIED BY 'hive_password';
create database oozie DEFAULT CHARACTER SET utf8;
grant all on oozie.* TO 'oozie'@'%' IDENTIFIED BY 'oozie_password';
grant all on oozie.* TO 'oozie'@'CDH' IDENTIFIED BY 'oozie_password';
create database metastore DEFAULT CHARACTER SET utf8;
grant all on metastore.* TO 'hive'@'%' IDENTIFIED BY 'hive_password';
grant all on metastore.* TO 'hive'@'CDH' IDENTIFIED BY 'hive_password';
GRANT ALL PRIVILEGES ON *.* TO 'root'@'%' IDENTIFIED BY 'gaoying' WITH GRANT OPTION;
flush privileges;
```
初始化数据库, 创建cloudera-scm用户
```
root@cdh-node-1:~# /opt/cm-5.6.0/share/cmf/schema/scm_prepare_database.sh mysql -uroot -p --scm-host localhost scm scm scm_password
root@cdh-node-1:~# useradd --system --home=/opt/cm-5.6.0/run/cloudera-scm-server --no-create-home --shell=/bin/false --comment "Cloudera SCM User" cloudera-scm
```
创建本地数据存储目录. 由于是单机安装, 就没有主从之分了.
```
root@cdh-node-1:~# mkdir /var/log/cloudera-scm-server
root@cdh-node-1:~# chown cloudera-scm:cloudera-scm /var/log/cloudera-scm-server
root@cdh-node-1:~#  vim /opt/cm-5.6.0/etc/cloudera-scm-agent/config.ini
//吧server_host改成主节点名称
server_host=cdh-node-1
```
安装依赖项
```
root@cdh-node-1:~# apt-get install lsb-base psmisc libsasl2-modules libsasl2-modules-gssapi-mit zlib1g libxslt1.1 libsqlite3-0 libfuse2 fuse rpcbind
```
启动cloudera-manage
```
root@cdh-node-1:~# /opt/cm-5.6.0/etc/init.d/cloudera-scm-server start
root@cdh-node-1:~# /opt/cm-5.6.0/etc/init.d/cloudera-scm-agent start
```

####安装Services####
这里才正式开始安装hadoop/hive 这些服务.
打开浏览器, 输入: cdh-node-1:7180;  初始登录用户名和密码为admin/admin
剩下的过程直接看截图即可.

###单机安装出现的问题###
1. Percentage under replicated blocks: 100.00%
参考: https://community.cloudera.com/t5/Cloudera-Manager-Installation/Percentage-under-replicated-blocks-100-00/td-p/26133
解决方案:
先把Replication Factor设置为1 . 在HDFS的configuration中设置: [HDFS]->[Configuration], 搜索replication即可找到该参数.
```
root@cdh-node-1:~# sudo -u oozie hadoop dfs -setrep -w 1 -R /
root@cdh-node-1:~# sudo -u oozie hdfs fsck / -delete

```



