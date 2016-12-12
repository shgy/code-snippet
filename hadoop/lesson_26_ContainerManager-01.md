1. startContainer()

   一次可启动多个container

   -- 获取待启动Container的列表
   -- 验证Token
   -- 发送ApplicationInitEvent事件
   -- 发送ApplicationContainerInitEvent事件

找到了一个执行命令的类: DefaultContainerExecutor.java
在这个类中会在每个节点中创建log-dir, 并执行我们传递给shell的命令
