一步一步: 服务器编程从易到难.

```
import com.google.protobuf.ByteString;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

/**
 *
 * 多线程线程的服务器, 改造成一个最简单的Http Server, 只会输出hello world, 能用浏览器访问.
 *
 */

public class SimpleServer3 {

    public static void main(String[] args) throws IOException{

        ServerSocket s = new ServerSocket(8189);
        while (true){
            System.out.println("client comes");
            Socket incoming = s.accept();

            new Thread(new ThreadedHttpHandler(incoming)).start();
        }

    }


    static class ThreadedHttpHandler implements Runnable{
        private Socket s;


        public ThreadedHttpHandler(Socket s) throws IOException {
            this.s = s;

        }
        @Override
        public void run() {
            try{

                InputStream inStream = s.getInputStream();
                OutputStream outStream = s.getOutputStream();
                int count = 0;
                while (count == 0) {
                    count = inStream.available();
                }
                byte[] b = new byte[count];
                inStream.read(b);
                System.out.println(new String(b));


                String dat = "hello world";
                outStream.write("HTTP/1.0 200 OK\r\n".getBytes());
                outStream.write("Content-Type: text/html\r\n".getBytes());
                outStream.write(("Content-length: " + dat.length() + "\r\n").getBytes());
                outStream.write("\r\n".getBytes());
                outStream.write((dat + "\r\n").getBytes());

                outStream.flush();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                try {
                    this.s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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