前面模仿Hadoop的RPC的调用方式， 自己实现一个RPC框架。
1. Socket编程就用最简单的单线程, 虽然与Hadoop的实现相差甚远， 但是我们的目的在于理解RPC调用的过程， 已经够用。
2. 使用动态代理获取到Client调用的方法及参数信息， 这是比较cool的地方， 是代理模式的典型应用。
```
    public static <T> T getProxy(Class<T> protocol){
        T proxy = (T)  Proxy.newProxyInstance(protocol.getClassLoader(), new Class[]{protocol}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                System.out.println(MessageFormat.format("method name is : {0}", method.getName()));
                Class<?>[] paramTypes = method.getParameterTypes();
                System.out.println(MessageFormat.format("paramCount is : {0}", paramTypes.length));
                for (int i = 0; i < paramTypes.length; i++) {
                    System.out.println(MessageFormat.format("the {0}th paramType is : {1}", i + 1, paramTypes[i].getCanonicalName()));
                    System.out.println(MessageFormat.format("the {0}th paramValue is: {1} ",i+1, args[i]));
                }


                return null;
            }
        });
        return proxy;
    }
```
3. Step 2中的信息如何传递到服务端呢？ 这就是WritableRpcEngine和ProtobufRpcEngine的职责所在了。以WritableRpcEngine为例， 来阐明hadoop的做法。

WritableRpcEngine.Invoker 承担的职责与 Step2 的 getProxy()方法中的匿名内部类一样， 不再赘述。
```
 new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                System.out.println(MessageFormat.format("method name is : {0}", method.getName()));
                Class<?>[] paramTypes = method.getParameterTypes();
                System.out.println(MessageFormat.format("paramCount is : {0}", paramTypes.length));
                for (int i = 0; i < paramTypes.length; i++) {
                    System.out.println(MessageFormat.format("the {0}th paramType is : {1}", i + 1, paramTypes[i].getCanonicalName()));
                    System.out.println(MessageFormat.format("the {0}th paramValue is: {1} ",i+1, args[i]));
                }


                return null;
            }
        });
```

4. 既然 Client调用的方法及参数信息 需要传输到Server端。 那么要解决的问题就是将这些信息序列化成二进制信息。这是一个编码的过程。
hadoop使用WriteableRpcEngine.Invocation类来抽象 Client调用的方法及参数信息。
```
  /** A method invocation, including the method name and its parameters.*/
  private static class Invocation implements Writable, Configurable {
    private String methodName;
    private Class<?>[] parameterClasses;
    private Object[] parameters;
    private Configuration conf;
    private long clientVersion;
    private int clientMethodsHash;
    private String declaringClassProtocolName;
```
Invocation类实现了Writable接口。这与待序列化的类需要实现Serializable接口是一样的。

5. 服务器端如何执行代理传递过来的方法？
   参考WritableRpcEngine.Server.WritableRpcInvoker.call()方法。

最后， 给出模仿Hadoop RPC自己动手实现的第一版RPC框架的代码。
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

interface MyProtocol{

    String echo(String msg);
    int hello(char a);
}

class MyProtocolImp implements MyProtocol{

    @Override
    public String echo(String msg) {
        return msg;
    }

    @Override
    public int hello(char a){
        return a;
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
        System.out.println(client.hello('a'));


        server.close();
    }
}

```



