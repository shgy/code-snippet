一步一步: 服务器编程从易到难.

```
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by shgy on 17-4-15.
 */
public class SimpleServer4 {
    public static void main(String[] args) throws IOException {

        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.socket().bind(new InetSocketAddress(8189), 10);
        channel.configureBlocking(false);
        Selector selector = Selector.open();

        channel.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {

            selector.select();
            Iterator<?> iterator = selector.selectedKeys().iterator();

            while (iterator.hasNext()) {

                SelectionKey key = (SelectionKey) iterator.next();
                iterator.remove(); // 删除此消息
                handlekey(key, selector);


            }

        }
    }

    public static void handlekey(SelectionKey key,Selector selector) throws IOException {

        ServerSocketChannel server = null;
        SocketChannel client = null;

        if (key.isAcceptable()) {

            System.out.println("Acceptable");
            server = (ServerSocketChannel) key.channel();
            client = server.accept();// 接受连接请求
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);

        } else if (key.isReadable()) {

            client = (SocketChannel) key.channel();
            //
            ByteBuffer byteBuffer = ByteBuffer.allocate(800);
            int count=client.read(byteBuffer);

            if(count>0){

                System.out.println("Readable");
                System.out.println(new String(byteBuffer.array()));

                String dat = "hello world";
                String sendString = "HTTP/1.0 200 OK\r\n"
                                    +"Content-Type: text/html\r\n"
                                    +"Content-length: " + dat.length() + "\r\n"
                                    +"\r\n"
                                    +dat+ "\r\n";
                ByteBuffer writeBuffer=ByteBuffer.wrap(sendString.getBytes());
                client.write(writeBuffer);


                key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            }else if(count ==-1){
                key.cancel();  //移除
                return;
            }


        }

    }
}
```

这个算是一个最简单的服务器了, 每次客户端请求:
1. 建立连接
2. 创建线程
3. 发送数据
4. 关闭连接

而其中, 连接/线程是可以复用的. 这里将作为改造的着力点.
参考 <Java核心技术>