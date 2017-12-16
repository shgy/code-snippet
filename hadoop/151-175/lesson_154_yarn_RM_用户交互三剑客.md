```
ClientRMService
AdminService
RMWebApp
```
ClientRMService 用户客户端和RM交互， 这个类应该是Yarn最常用的类了。用户提交App, 查看App状态， 终止App都靠它。

AdminService 管理员的操作， 比如 `refreshQueues` 等

RMWebApp 以Web网页的形式 展示集群的状态。

