由于集群安装在笔记本上. 在办公环境中IP很容易被修改. 
IP修改后,集群就无法正常运行了, 这是因为CM在scm库的HOSTS表中记录了IP
使用命令:
```
mysql -uroot -ppasswd scm -e "update HOSTS set IP_ADDRESS='192.168.2.5'" 
```
修改IP, 然后重启集群即可. 如果不能确保进程都杀干净了,就重启机器吧.
