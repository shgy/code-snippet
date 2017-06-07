前面对RPC的序列化机制WritableRpcEngine和ProtobufRpcEngine进行了学习。接下来学习RPC的网络处理部分。由于Client比较简单，
先拿软柿子捏吧。

1. Client中使用`Hashtable<ConnectionId, Connection> connections` 来存储客户端和所有服务端的连接。
2. RpcEngine使用`HashMap<SocketFactory, Client> clients` 来存储客户端的对象。 why ?

ConnectionId 中的hashCode的prime=16777619
FNV_prime的取值:
32 bit FNV_prime = 2^24 + 2^8 + 0x93 = 16777619
64 bit FNV_prime = 2^40 + 2^8 + 0xb3 = 1099511628211
128 bit FNV_prime = 2^88 + 2^8 + 0x3b = 309485009821345068724781371

模仿Hadoop的ImitateRPC中， 客户端往服务端发消息的代码位于匿名内部类`InvocationHandler`中。
这样显然是过于粗糙了。 客户端有多个RPC请求怎么办呢？ 像ImitateRPC这样一一排队性能就太低了。 完全不符合资本家的特征，
对机器丝毫没有一点点压榨。 Hadoop的做法就抽象出了一个Client类。 所有的操作都由Client来处理。