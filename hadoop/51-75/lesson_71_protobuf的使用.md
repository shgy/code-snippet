hadoop 生产环境中默认使用protobuf来序列化对象。 protobuf是如何使用的呢？

Step1. 安装protobuf的依赖包
```
wget http://protobuf.googlecode.com/files/protobuf-2.5.0.tar.gz
tar -xf protobuf-2.5.0.tar.gz
cd protobuf
./configure
make
make check
make install
```

Step2. 定义相关的消息格式。 我是直接从hadoop-common的源码中拿了两个文件。 test.proto和test_rpc_service.proto
```
src/main/java/
├── hadooprpc
│   └── demo
│       └── RPCDemo.java
├── proto
│   ├── test.proto
│   └── test_rpc_service.proto
├── srpc
│   ├── ImitateRPC.java
│   ├── readme.md
│   ├── TestProtos.java
│   └── TestRpcServiceProtos.java
└
```
文件的包地址进行简单的修改
```
$ cat test.proto
//option java_package = "srpc";
option java_outer_classname = "TestProtos";
option java_generate_equals_and_hash = true;
package srpc;

message EmptyRequestProto {
}

message EmptyResponseProto {
}

message EchoRequestProto {
    required string message = 1;
}

message EchoResponseProto {
    required string message = 1;
}
```
```
$ cat test_rpc_service.proto
//option java_package = "srpc";
option java_outer_classname = "TestRpcServiceProtos";
option java_generic_services = true;
option java_generate_equals_and_hash = true;
package srpc;
//
import "test.proto";


/**
 * A protobuf service for use in tests
 */
service TestProtobufRpcProto {
    rpc ping(EmptyRequestProto) returns (EmptyResponseProto);
    rpc echo(EchoRequestProto) returns (EchoResponseProto);
    rpc error(EmptyRequestProto) returns (EmptyResponseProto);
    rpc error2(EmptyRequestProto) returns (EmptyResponseProto);
}

service TestProtobufRpc2Proto {
    rpc ping2(EmptyRequestProto) returns (EmptyResponseProto);
    rpc echo2(EchoRequestProto) returns (EchoResponseProto);
}
```
Step3. 生成Java代码。
```
protoc --java_out ../ ./test.proto
protoc --java_out ../ ./test_rpc_service.proto
```

Step4. 编写服务器和客户端代码
```
/**
 * Created by shgy on 17-5-16.
 */
package hadooprpc.demo;

import com.google.protobuf.BlockingService;
import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;
import srpc.TestProtos;
import srpc.TestRpcServiceProtos;

import java.io.IOException;
import java.net.InetSocketAddress;

interface ClientProtocol extends TestRpcServiceProtos.TestProtobufRpcProto.BlockingInterface {
    // 版本号,默认情况下,不同版本号的 RPC Client 和 Server 之间不能相互通信
    public static final long versionID = 1L;
}

class ClientProtocolImpl implements ClientProtocol {

    @Override
    public TestProtos.EmptyResponseProto ping(RpcController controller, TestProtos.EmptyRequestProto request) throws ServiceException {
        return null;
    }

    @Override
    public TestProtos.EchoResponseProto echo(RpcController controller, TestProtos.EchoRequestProto request) throws ServiceException {
        return TestProtos.EchoResponseProto.newBuilder().setMessage(request.getMessage())
                .build();
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
public class RPCDemo {
    public static void main(String[] args) throws HadoopIllegalArgumentException, IOException, ServiceException {
        Configuration conf = new Configuration();
        RPC.setProtocolEngine(conf, ClientProtocol.class, ProtobufRpcEngine.class);

        ClientProtocolImpl serverImpl = new ClientProtocolImpl();
        BlockingService service = TestRpcServiceProtos.TestProtobufRpcProto
                .newReflectiveBlockingService(serverImpl);

        Server server = new RPC.Builder(conf).setProtocol(ClientProtocol.class)
                .setInstance(service).setBindAddress("localhost").setPort(8097)
                .setNumHandlers(5).build();
        server.start();

        ClientProtocol client = (ClientProtocol)RPC.getProxy(ClientProtocol.class, ClientProtocol.versionID,
                new InetSocketAddress(8097), new Configuration());

        TestProtos.EchoRequestProto msg = TestProtos.EchoRequestProto.newBuilder().setMessage("hello").build();
        TestProtos.EchoResponseProto echoResult = client.echo(null, msg);
        System.out.println(echoResult.getMessage());
        RPC.stopProxy(client);
    }
}

```

与使用WritableRpcEngine相比， 这里的代码有些不同。
```
ClientProtocolImpl serverImpl = new ClientProtocolImpl();
BlockingService service = TestRpcServiceProtos.TestProtobufRpcProto
        .newReflectiveBlockingService(serverImpl);
```

上文中使用protobuf的代码比较原始， 耦合度特别高。 而且， hadoop中也不是这样使用的。


使用过程中的问题排查
1. java包路径和proto文件的命名空间。只要统一两边的依赖和命名空间以及正确设置好java包路径即可，
在proto文件中，如果存在package声明无java_package声明，则说明该proto文件所在命名空间和生成的java包路径是一样的，
如果有java_package则这个声明的为生成的java包路径，pacakge则只表示proto文件的命名空间，不表示java包路径！
2、两个文件的依赖关系（import）。即import引用的文件代表依赖关系，且import引用的要在对应的命名空间里引用，
比如两个文件都在同个目录下，可以直接引用import "descriptor.proto"
参考：
http://blog.csdn.net/lufeng20/article/details/11157313