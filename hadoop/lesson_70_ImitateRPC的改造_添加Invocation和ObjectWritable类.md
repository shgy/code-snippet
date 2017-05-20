前面很粗糙的实现了Hadoop RPC框架的模仿版ImitateRPC. 代码在实现的过程中， 有部分逻辑可以抽象出来。
依然仿照Hadoop RPC， 抽离出来了两个类Invocation和ObjectWritable。
一则代码的逻辑清晰了很多， 二则对Hadoop中Invocation和ObjectWritable类的作用有更深入的理解。

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

    private static class ObjectWritable{
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

        private Class declaredClass;
        private Object instance;

        public ObjectWritable(){}

        public static void writeObject(DataOutput out, Object instance, Class declaredClass) throws IOException {
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

        public static Object readObject(DataInput in, ObjectWritable objectWritable) throws IOException {
            String className = in.readUTF();
            Class<?> declaredClass = PRIMITIVE_NAMES.get(className);
            if(declaredClass == null){
                declaredClass = String.class;
            }
            Object instance = null;
            if(declaredClass == String.class){
                instance = in.readUTF();
            }else if (declaredClass.isPrimitive()) {            // primitive types

                if (declaredClass == Boolean.TYPE) {             // boolean
                    instance = Boolean.valueOf(in.readBoolean());
                } else if (declaredClass == Character.TYPE) {    // char
                    instance = Character.valueOf(in.readChar());
                } else if (declaredClass == Byte.TYPE) {         // byte
                    instance = Byte.valueOf(in.readByte());
                } else if (declaredClass == Short.TYPE) {        // short
                    instance = Short.valueOf(in.readShort());
                } else if (declaredClass == Integer.TYPE) {      // int
                    instance = Integer.valueOf(in.readInt());
                } else if (declaredClass == Long.TYPE) {         // long
                    instance = Long.valueOf(in.readLong());
                } else if (declaredClass == Float.TYPE) {        // float
                    instance = Float.valueOf(in.readFloat());
                } else if (declaredClass == Double.TYPE) {       // double
                    instance = Double.valueOf(in.readDouble());
                } else if (declaredClass == Void.TYPE) {         // void
                    instance = null;
                } else {
                    throw new IllegalArgumentException("Not a primitive: "+declaredClass);
                }
            }

            if(objectWritable!=null){
                objectWritable.declaredClass = declaredClass;
                objectWritable.instance = instance;
            }

            return instance;
        }
    }

    private static class Invocation{
        /**
         *
         * 封装调用方法
         *
         * */
        private String methodName;
        private Class<?>[] parameterClasses;
        private Object[] parameters;

        public Invocation(){}

        public Invocation(Method method, Object[] parameters) {
            this.methodName = method.getName();
            this.parameterClasses = method.getParameterTypes();
            this.parameters = parameters;
        }

        public void readFields(DataInput in) throws IOException {
            this.methodName = in.readUTF();
            int paramCount = in.readInt();
            Class<?>[] parameterClasses = new Class<?>[paramCount];
            Object[] parameters = new Object[paramCount];
            ObjectWritable objectWritable = new ObjectWritable();
            for(int i=0;i<paramCount;i++){
                ObjectWritable.readObject(in,objectWritable);
                parameterClasses[i] = objectWritable.declaredClass;
                parameters[i] = objectWritable.instance;
            }

            this.parameterClasses = parameterClasses;
            this.parameters = parameters;
        }

        public void write(DataOutput out) throws IOException {
            out.writeUTF(methodName);  //  写入方法名
            out.writeInt(parameterClasses.length); // 写入参数个数
            // 写入参数
            for (int i = 0; i < parameterClasses.length; i++) {
               ObjectWritable.writeObject(out, parameters[i],  parameterClasses[i]);
            }
        }
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

                            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));

                            Invocation invocation = new Invocation();
                            invocation.readFields(in);

                            // 调用 相关函数
                            Method method = protocolClass.getMethod(invocation.methodName, invocation.parameterClasses);
                            Object retVal = method.invoke(protocolImpl, invocation.parameters);
                            Class retType = method.getReturnType();

                            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                            DataOutput out = new DataOutputStream(buffer);
                            ObjectWritable.writeObject(out,retVal, retType);

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

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                DataOutput out = new DataOutputStream(buffer);

                Invocation invocation = new Invocation(method, args);
                invocation.write(out);

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

                    DataInputStream in = new DataInputStream(new ByteArrayInputStream(retArray));
                    return ObjectWritable.readObject(in, null);
                }finally {
                    try {
                        client.close();
                    }catch (Exception e){}
                }
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
        System.out.println(client.echo("hello java"));
        System.out.println(client.add(1, 4));

        server.close();
    }
}
```
但是ObjectWritable 有些名不副实。只能处理基本类型和字符串。 对于对象的序列化， hadoop并没有实现Serializable接口，
而是自己定义了一个Writable接口。 相关的类实现接口方法即可。