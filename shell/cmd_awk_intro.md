需求: 在部署CDH集群的服务器上运行着许多服务, 找出Java相关服务的PID
```
 netstat -nltp | grep java  | awk '{sub(/\/java/,"");print $7}'
```
这里用到了awk的sub函数.
