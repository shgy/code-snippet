在Hadoop中，在客户端拿到RPC的代理类方法如下： `RPC.getProxy()`. 感觉挺简单， 没想到这个方法颇有些曲径通幽的神韵。
调用过程如下：
```
--RPC.getProxy()
----> getProtocolProxy().getProxy()
------> getProtocolEngine().getProxy()
```
以WritableRPCEngine.getProxy()为例， 其实现的方法如下：
```
    T proxy = (T) Proxy.newProxyInstance(protocol.getClassLoader(),
        new Class[] { protocol }, new Invoker(protocol, addr, ticket, conf,
            factory, rpcTimeout, fallbackToSimpleAuth));
    return new ProtocolProxy<T>(protocol, proxy, true);
```

直接拿到RPCEngine也可以实现该功能， 为啥要添加`ProtocolProxy`这个类呢？
首先看ProtocolProxy的功能： 检测服务端是否支持客户端将调用的方法。  仿造的时候可以先将架子搭起， 用到时在实现其功能。


