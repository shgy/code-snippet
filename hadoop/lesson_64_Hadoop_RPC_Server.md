在hadoop-common的TestRPC类中, 讲述了Server的使用样例. Server是整个hadoop的基石, 基于JAVA NIO构建.
Server由Listener/Handler/Responser三部分组成. 分工清晰且专业.

# Listener

Listener负责接收并读取外部的连接请求:

`doAccept(key)` 生成 Connection对象. 然后添加到内部Reader的pendingConnections队列中.
Listener.Reader类负责从pendingConnections获取Connection对象, 并读取的客户端传递的信息,
解析成Call对象, 添加到callQueue中.

# Handler

Handler负责从callQueue中获取并处理Caller对象. 然后执行相应的方法(远程调用的核心).
然后调用`responder.doRespond(call)`将剩下的任务移交给responder对象

# responder

Responder负责将RPC执行的结果回写到客户端.

这样, 在3个伙伴的密切配合下, 一次远程调用就完美地执行了.

接下来将分析客户端的流程, 理解透彻后将尝试仿照hadoop-common的功能,
自己仿造一个简单的PRC框架, 实现通过protobuf进行远程调用的功能.