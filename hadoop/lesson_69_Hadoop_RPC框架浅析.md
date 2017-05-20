hadoop作为分布式的系统， 集群机器之间的通信是最基本，最常见的需求。
这种需求本质上是IPC, 即进程间通信。 按照传统的UINX编程模型，进程间通信无非是如下的几种方式:
管道， FIFO， 消息队列， 信号量， 共享存储， 套接字。只有套接字是可以跨机器的网络通信， 能满足hadoop的需求。

通常情况下， 网络通信的程序使用显式网络编程（即直接使用java.net包）。比如Web浏览器， Web服务器等。
但也有另一部分程序使用隐式网络编程， 比如利用hadoop RPC这种封装了底层通信细节的工具包，
使得底层的网络通信对于程序员透明。这样做一则减轻了程序员的负担， 二则抽象了功能模块， 使得模块之间职责更清晰， 便于维护。

首先展示一个hadoop RPC功能demo， 了解hadoop RPC的用法。

step 1: 在pom.xml文件中添加依赖项
```
  <dependency>
       <groupId>org.apache.hadoop</groupId>
       <artifactId>hadoop-common</artifactId>
       <version>2.6.0</version>
   </dependency>
```

step 2: 在src/main/resources中添加log4j.properties
```
log4j.rootLogger=DEBUG,console
log4j.additivity.org.apache=true
# (console)
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.Threshold=INFO
log4j.appender.console.ImmediateFlush=true
log4j.appender.console.Target=System.err
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=[%-5p] %d(%r) --> [%t] %l: %m %x %n
```

step 3: 定义RPC协议
```
package hadooprpc.demo;

import java.io.IOException;

interface ClientProtocol extends org.apache.hadoop.ipc.VersionedProtocol {
	// 版本号,默认情况下,不同版本号的 RPC Client 和 Server 之间不能相互通信
	public static final long versionID = 1L;
	String echo(String value) throws IOException;
	int add(int v1, int v2) throws IOException;
}
```

step 4: 实现RPC协议
```
package hadooprpc.demo;

import java.io.IOException;

import org.apache.hadoop.ipc.ProtocolSignature;

public  class ClientProtocolImpl implements ClientProtocol {
    // 重载的方法,用于获取自定义的协议版本号,
	public long getProtocolVersion(String protocol, long clientVersion) {
		return ClientProtocol.versionID;
	}

	// 重载的方法,用于获取协议签名
	public ProtocolSignature getProtocolSignature(String protocol, long clientVersion,
	int hashcode) {
		return new ProtocolSignature(ClientProtocol.versionID, null);
	}

	public String echo(String value) throws IOException {
		return value;
	}

	public int add(int v1, int v2) throws IOException {
		return v1 + v2;
	}
}
```

step 5； 构造并启动 RPC Server
```
package hadooprpc.demo;

import java.io.IOException;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;

public class RPCServer {
	public static void main(String[] args) throws HadoopIllegalArgumentException, IOException {
		Configuration conf = new Configuration();
		Server server = new RPC.Builder(conf).setProtocol(ClientProtocol.class)
				.setInstance(new ClientProtocolImpl()).setBindAddress("localhost").setPort(8097)
				.setNumHandlers(5).build();
				server.start();
	}
}

```

step 6: 构造 RPC Client 并发送 RPC 请求
```
package hadooprpc.demo;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

public class RPCClient {
	public static void main(String[] args) throws IOException {
		ClientProtocol client = (ClientProtocol)RPC.getProxy(ClientProtocol.class, ClientProtocol.versionID,
		                                                     new InetSocketAddress(8097), new Configuration());
		int result = client.add(5, 6);
		System.out.println(result);
		String echoResult = client.echo("result");
		System.out.println(echoResult);
		RPC.stopProxy(client); -- 关闭连接
	}
}
```

step 7: 启动RPC Server, 然后执行 RPC Client.

通过上面的例子可以发现， 通过这种编程方式，不用考虑网络层的细节，只需要编写接口和接口实现即可。
问渠那得清如许？  通过hadoop RPC的源码， 或许可以管中窥豹。

hadoop RPC的实现原理很简单。
Client:
 1. 通过动态代理，获取到调用接口的方法，参数类型。
 2. 将调用信息编码，发送到服务器
 3. 获取服务器的返回值， 并解码。
 4. 返回调用方法的返回值。
Server:
 1. 启动服务器， 并监听客户端。
 2. 获取客户端发送过来的调用方法， 参数。
 3. 执行实现类中相关的方法。
 4. 将返回值发送到客户端。

理解原理后， 自己动手实现了一个非常粗糙， 只具备演示功能的RPC框架。
```
package srpc;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shgy on 17-5-7.
 * 创建一个简单的PRC框架, 参数和返回值只支持基础类型
 */

// 接口
interface MyProtocol{

    String echo(String msg);
    int add(int a,int b);
}

// 实现
class MyProtocolImp implements MyProtocol{

    @Override
    public String echo(String msg) {
        return msg;
    }

    @Override
    public int add(int a,int b){
        return a+b;
    }
}

public class ImitateRPC {
    private static final Map<String, Class<?>> PRIMITIVE_NAMES = new HashMap<String, Class<?>>();
    static {
        PRIMITIVE_NAMES.put("boolean", Boolean.TYPE);
        PRIMITIVE_NAMES.put("byte", Byte.TYPE);
        PRIMITIVE_NAMES.put("char", Character.TYPE);
        PRIMITIVE_NAMES.put("short", Short.TYPE);
        PRIMITIVE_NAMES.put("int", Integer.TYPE);
        PRIMITIVE_NAMES.put("long", Long.TYPE);
        PRIMITIVE_NAMES.put("float", Float.TYPE);
        PRIMITIVE_NAMES.put("double", Double.TYPE);
        PRIMITIVE_NAMES.put("void", Void.TYPE);
    }
    public static class Server{


        private Class<?> protocolClass;

        private Object protocolImpl;

        private ServerSocket server;

        public  Server(Class<?> protocolClass, Object protocolImpl) throws IOException {

            if(protocolImpl == null){
                throw new IllegalArgumentException("protocolImpl is not set");
            }
            if (protocolClass == null) {
                throw new IllegalArgumentException("protocolClass is not set");

            } else {
                if (!protocolClass.isAssignableFrom(protocolImpl.getClass())) {
                    throw new IOException("protocolClass "+ protocolClass +
                            " is not implemented by protocolImpl which is of class " +
                            protocolImpl.getClass());
                }
            }

            this.protocolClass = protocolClass;
            this.protocolImpl = protocolImpl;


        }

        public void start(){
            System.out.println("start server");
            try{
                this.server = new ServerSocket(8189);
                listen();
            }catch(Exception e){
                e.printStackTrace();
            }

        }


        public void close(){
            System.out.println("close server");
            try {
                this.server.close();
            } catch (IOException e) {}
        }

        private void listen(){
            new Thread(){
                @Override
                public void run(){
                    while (!server.isClosed()){

                        Socket incoming = null;
                        try {
                            incoming = server.accept();
                            DataInputStream inStream = new DataInputStream(incoming.getInputStream());

                            // 从客户端读取出 调用方法的信息
                            int dataLen = inStream.readInt();
                            byte[] data = new byte[dataLen];
                            inStream.read(data,0, dataLen);

                            DataInputStream contentStream = new DataInputStream(new ByteArrayInputStream(data));
                            String methodName = contentStream.readUTF();
                            int paramCount = contentStream.readInt();
                            Class<?>[] paramTypes = new Class<?>[paramCount];
                            Object[] args = new Object[paramCount];
                            for(int i=0;i<paramCount;i++){
                                String className = contentStream.readUTF();
                                Class<?> declaredClass = PRIMITIVE_NAMES.get(className);
                                if(declaredClass == null){
                                    declaredClass = String.class;
                                }
                                paramTypes[i] = declaredClass;
                                if(declaredClass == String.class){
                                    args[i] = contentStream.readUTF();
                                }else if (declaredClass.isPrimitive()) {            // primitive types

                                    if (declaredClass == Boolean.TYPE) {             // boolean
                                        args[i] = Boolean.valueOf(contentStream.readBoolean());
                                    } else if (declaredClass == Character.TYPE) {    // char
                                        args[i] = Character.valueOf(contentStream.readChar());
                                    } else if (declaredClass == Byte.TYPE) {         // byte
                                        args[i] = Byte.valueOf(contentStream.readByte());
                                    } else if (declaredClass == Short.TYPE) {        // short
                                        args[i] = Short.valueOf(contentStream.readShort());
                                    } else if (declaredClass == Integer.TYPE) {      // int
                                        args[i] = Integer.valueOf(contentStream.readInt());
                                    } else if (declaredClass == Long.TYPE) {         // long
                                        args[i] = Long.valueOf(contentStream.readLong());
                                    } else if (declaredClass == Float.TYPE) {        // float
                                        args[i] = Float.valueOf(contentStream.readFloat());
                                    } else if (declaredClass == Double.TYPE) {       // double
                                        args[i] = Double.valueOf(contentStream.readDouble());
                                    } else if (declaredClass == Void.TYPE) {         // void
                                        args[i] = null;
                                    } else {
                                        throw new IllegalArgumentException("Not a primitive: "+declaredClass);
                                    }
                                }
                            }

                            Method method = protocolClass.getMethod(methodName, paramTypes);
                            Object obj = method.invoke(protocolImpl, args);
                            Class retType = method.getReturnType();

                            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                            DataOutput out = new DataOutputStream(buffer);
                            out.writeUTF(retType.getCanonicalName());
                            if(retType == String.class){
                                out.writeUTF((String)obj);
                            }else if (retType.isPrimitive()) {            // primitive types

                                if (retType == Boolean.TYPE) {             // boolean
                                    out.writeBoolean((Boolean)obj);
                                } else if (retType == Character.TYPE) {    // char
                                    out.writeChar((Character) obj);
                                } else if (retType == Byte.TYPE) {         // byte
                                    out.writeBoolean((Boolean)obj);
                                } else if (retType == Short.TYPE) {        // short
                                    out.writeShort((Short) obj);
                                } else if (retType == Integer.TYPE) {      // int
                                    out.writeInt((Integer) obj);
                                } else if (retType == Long.TYPE) {         // long
                                    out.writeLong((Long) obj);
                                } else if (retType == Float.TYPE) {        // float
                                    out.writeFloat((Float) obj);
                                } else if (retType == Double.TYPE) {       // double
                                    out.writeDouble((Double) obj);
                                } else if (retType == Void.TYPE) {         // void
                                } else {
                                    throw new IllegalArgumentException("Not a primitive: "+retType);
                                }
                            }
                            byte[] array = buffer.toByteArray();
                            //将返回结果写回到客户端
                            DataOutputStream outStream = new DataOutputStream(incoming.getOutputStream());
                            outStream.writeInt(array.length);
                            outStream.write(array);
                            outStream.flush();
                        }catch (SocketException e){
                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            try {
                                if(incoming!=null){ incoming.close();  }
                            }catch (Exception e){}
                        }

                    }
                }
            }.start();
        }
    }



    public static <T> T getProxy(Class<T> protocol){
        T proxy = (T)  Proxy.newProxyInstance(protocol.getClassLoader(), new Class[]{protocol},
                new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                Class<?>[] paramTypes = method.getParameterTypes();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                DataOutput out = new DataOutputStream(buffer);
                out.writeUTF(method.getName());  //  写入方法名
                out.writeInt(paramTypes.length); // 写入参数个数

                // 写入参数
                for (int i = 0; i < paramTypes.length; i++) {
                    Class<?> declaredClass = paramTypes[i];
                    Object instance = args[i];
                    String paramTypeName = declaredClass.getCanonicalName();
                    out.writeUTF(paramTypeName);
                    if (declaredClass == String.class) {
                        out.writeUTF((String)instance);
                    }else if (declaredClass.isPrimitive()) {     // primitive type

                        if (declaredClass == Boolean.TYPE) {        // boolean
                            out.writeBoolean(((Boolean) instance).booleanValue());
                        } else if (declaredClass == Character.TYPE) { // char
                            out.writeChar(((Character) instance).charValue());
                        } else if (declaredClass == Byte.TYPE) {    // byte
                            out.writeByte(((Byte) instance).byteValue());
                        } else if (declaredClass == Short.TYPE) {   // short
                            out.writeShort(((Short) instance).shortValue());
                        } else if (declaredClass == Integer.TYPE) { // int
                            out.writeInt(((Integer) instance).intValue());
                        } else if (declaredClass == Long.TYPE) {    // long
                            out.writeLong(((Long) instance).longValue());
                        } else if (declaredClass == Float.TYPE) {   // float
                            out.writeFloat(((Float) instance).floatValue());
                        } else if (declaredClass == Double.TYPE) {  // double
                            out.writeDouble(((Double) instance).doubleValue());
                        } else if (declaredClass == Void.TYPE) {    // void
                        } else {
                            throw new IllegalArgumentException("Not a primitive: " + declaredClass);
                        }
                    }else{
                        throw new IOException("Can't write: "+instance+" as "+declaredClass);
                    }
                }

                // 发送到服务器端
                byte[] array = buffer.toByteArray();

                Socket client = new Socket("127.0.0.1", 8189);
                try{
                   DataOutputStream outStream = new DataOutputStream(client.getOutputStream());
                    outStream.writeInt(array.length);
                    outStream.write(array);
                    outStream.flush();

                    // 读取服务器的应答
                    DataInputStream inStream = new DataInputStream(client.getInputStream());
                    int bufSize = inStream.readInt();
                    byte[] retArray = new byte[bufSize];
                    inStream.read(retArray,0, bufSize);

                    DataInputStream contentStream = new DataInputStream(new ByteArrayInputStream(retArray));
                    String retType = contentStream.readUTF();
                    Class retTypeClass = PRIMITIVE_NAMES.get(retType);
                    if(retTypeClass == null){
                        retTypeClass = String.class;
                    }
                    if(retTypeClass == String.class){
                        return contentStream.readUTF();
                    }else if (retTypeClass.isPrimitive()) {            // primitive types

                        if (retTypeClass == Boolean.TYPE) {             // boolean
                            return Boolean.valueOf(contentStream.readBoolean());
                        } else if (retTypeClass == Character.TYPE) {    // char
                            return Character.valueOf(contentStream.readChar());
                        } else if (retTypeClass == Byte.TYPE) {         // byte
                            return Byte.valueOf(contentStream.readByte());
                        } else if (retTypeClass == Short.TYPE) {        // short
                            return Short.valueOf(contentStream.readShort());
                        } else if (retTypeClass == Integer.TYPE) {      // int
                            return Integer.valueOf(contentStream.readInt());
                        } else if (retTypeClass == Long.TYPE) {         // long
                            return Long.valueOf(contentStream.readLong());
                        } else if (retTypeClass == Float.TYPE) {        // float
                            return Float.valueOf(contentStream.readFloat());
                        } else if (retTypeClass == Double.TYPE) {       // double
                            return Double.valueOf(contentStream.readDouble());
                        } else if (retTypeClass == Void.TYPE) {         // void
                            return null;
                        } else {
                            throw new IllegalArgumentException("Not a primitive: "+retTypeClass);
                        }
                    }
                }finally {
                    try {
                        client.close();
                    }catch (Exception e){}
                }


                return null;
            }
        });
        return proxy;
    }

    public static void main(String[] args) throws IOException {
        // start server
        Server server = new Server(MyProtocol.class, new MyProtocolImp());
        server.start();

        // call rpc
        MyProtocol client = ImitateRPC.getProxy(MyProtocol.class);
        System.out.println(client.echo("hello world"));
        System.out.println(client.add(1, 4));


        server.close();
    }
}

```

上面的代码只有300多行， 而且在编码/解码部分有重叠的代码， 可以优化缩减， 功能也不完善。 但是仿照Hadoop RPC, 演示了
RPC的原理。 对照着Hadoop RPC的源码， 自己编码实现， 对于源码中各个类的功能， 会有更清晰的理解。











