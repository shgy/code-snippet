在《Hadoop 2.x HDFS源码剖析》中讲到了ClientProtol.rename()方法的调用，讲到了适配器模式。
前面也写到了protobuf的使用， 但是有个问题就是方法的参数都是Protobuf的一套, 处理起来比较繁琐。
以echo方法为例：我们期望的接口是这样的。
```
	String echo(String value) throws IOException;
```
protobuf的接口是这样的
```
 TestProtos.EchoResponseProto echo(RpcController controller, TestProtos.EchoRequestProto request);
```

怎么办呢， 使用Java设计模式中的适配器模式 做一个转换。 源接口的定义不变
```
interface ClientProtocol{

    String echo(String msg) throws ServiceException;
}

class ClientProtocolImpl implements ClientProtocol{

    @Override
    public String echo(String msg) throws ServiceException {
        return msg;
    }
}
```
该接口ProtobufRpc是没法直接使用的， 因为不符合它的规范。定义新的符合protobuf规范的接口。 如下：
```

interface ClientProtocolPB extends TestRpcServiceProtos.TestProtobufRpcProto.BlockingInterface {
    // 版本号,默认情况下,不同版本号的 RPC Client 和 Server 之间不能相互通信
    public static long versionID = 1L;
}
```
然后Client和Server端分别实现一套代码逻辑，

在客户端,适配器的echo方法对参数进行了封装

```
class ClientProtocolTranslatorPB implements ClientProtocol{

    private ClientProtocolPB proxy;

    public ClientProtocolTranslatorPB() throws IOException {
        this.proxy =  RPC.getProxy(ClientProtocolPB.class, ClientProtocolPB.versionID,
                new InetSocketAddress(8097), new Configuration());
    }

    @Override
    public String echo(String msg) throws ServiceException {
        TestProtos.EchoRequestProto protoMsg = TestProtos.EchoRequestProto.newBuilder().setMessage("hello").build();
        TestProtos.EchoResponseProto echoResult = proxy.echo(null, protoMsg);
        return echoResult.getMessage();
    }
}
```

在服务端， 适配器的echo方法对参数进行了拆包， 然后调用 `ClientProtocolImpl.echo()`方法。

```
class ClientProtocolServerSideTranslatorPB implements ClientProtocolPB {

    private ClientProtocol proxy;

    public ClientProtocolServerSideTranslatorPB(ClientProtocol proxy){
        this.proxy = proxy;
    }

    @Override
    public TestProtos.EmptyResponseProto ping(RpcController controller, TestProtos.EmptyRequestProto request) throws ServiceException {
        return null;
    }

    @Override
    public TestProtos.EchoResponseProto echo(RpcController controller, TestProtos.EchoRequestProto request) throws ServiceException {
        String msg = request.getMessage();
        String resp = this.proxy.echo(msg);
        return TestProtos.EchoResponseProto.newBuilder().setMessage(resp).build();
    }

    @Override
    public TestProtos.EmptyResponseProto error(RpcController controller, TestProtos.EmptyRequestProto request) throws ServiceException {
        return null;
    }

    @Override
    public TestProtos.EmptyResponseProto error2(RpcController controller, TestProtos.EmptyRequestProto request) throws ServiceException {
        return null;
    }
}
```

最后， main方法的代码如下：
```
public class RPCDemo {
    public static void main(String[] args) throws HadoopIllegalArgumentException, IOException, ServiceException {
        Configuration conf = new Configuration();
        RPC.setProtocolEngine(conf, ClientProtocolPB.class, ProtobufRpcEngine.class);
        ClientProtocolServerSideTranslatorPB serverImpl =
         new ClientProtocolServerSideTranslatorPB(new ClientProtocolImpl());

        BlockingService service = TestRpcServiceProtos.TestProtobufRpcProto
                .newReflectiveBlockingService(serverImpl);

        Server server = new RPC.Builder(conf).setProtocol(ClientProtocolPB.class)
                .setInstance(service).setBindAddress("localhost").setPort(8097)
                .setNumHandlers(5).build();
        server.start();

        ClientProtocol client = new ClientProtocolTranslatorPB();

        System.out.println(client.echo("hello"));
        server.stop();
    }
}
```