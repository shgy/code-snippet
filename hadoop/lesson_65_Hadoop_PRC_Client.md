RPC的Client没有Server那么复杂. 参考: TestRPC.testCalls()方法.
1. 底层的网络通信  Client.Connection. Connection类的run()方法负责接收RPC调用返回的消息.
2. 对象的序列化:   ProtobufRPCEngine/WritablePRCEngine
3. 远程方法的调用:  使用了动态代理设计模式, 使得具体的实现对客户端完全透明. 参考类: RPC

