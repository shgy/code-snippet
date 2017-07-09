ResourceManager 作为一个管理者， 负责统筹。 更多的时候是
1. 记录各个NodeManager运行的状态。
2. 向NodeManager发出执行的指令。
3. 对NodeManager汇报的信息予以反馈。


用户提交一个任务， 在ResourceManager端， 由RMAppManager负责执行具体的操作，
1. RMAppManager.submitApplication()将任务信息记录在rmContext中
2. 向RMAppImpl发送消息 RMAppEventType.START