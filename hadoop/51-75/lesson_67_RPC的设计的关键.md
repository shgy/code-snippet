分布式系统, 需要在不同的实体中显示交换消息, 处理诸如消息的编解码, 消息的发送和接收等具体的任务.
如何隐藏底层的通信细节, 是实现分布式系统访问透明性的重要议题之一.

在Java语言中, 实现分布式系统访问透明性的一种手段就是动态代理。 下面的代码模仿hadoop的RPC实现
```
package srpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;

/**
 * Created by shgy on 17-5-7.
 * 创建一个简单的PRC框架, 参数和返回值只支持基础类型
 */

interface MyProtocol{

    String echo(String msg, int type);
}

class MyProtocolImp implements MyProtocol{

    @Override
    public String echo(String msg, int type) {
        return msg;
    }
}

public class ImitateRPC {

    public static class Server{

        private Class<?> protocolClass;

        private Object protocolImpl;

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
                final ServerSocket s = new ServerSocket(8189);
                listen(s);
            }catch(Exception e){
                e.printStackTrace();
            }

        }

        private void listen(final ServerSocket s){
            new Thread(){
                @Override
                public void run(){
                    while (true){

                        Socket incoming = null;
                        try {
                            incoming = s.accept();
                            InputStream inStream = incoming.getInputStream();
                            OutputStream outStream = incoming.getOutputStream();

                            // 从客户端读取出 调用方法的信息


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

    public static void main(String[] args) throws IOException {
        // start server
//        Server server = new Server(MyProtocol.class, new MyProtocolImp());
//        server.start();

        // call rpc
        MyProtocol client = ImitateRPC.getProxy(MyProtocol.class);
        client.echo("hello world", 100);
    }
}
```

通过这种模仿， 更容易体会到hadoop中各个类的作用。
