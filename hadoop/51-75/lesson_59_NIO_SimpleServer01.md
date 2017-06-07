一步一步: 服务器编程从易到难.

```
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
/**
 *
 * 单线程的服务器, 每次只能服务一个客户. 有点像以前火车票代售点的窗口.
 *
 */

class SimpleServer{

    public static void main(String[] args) throws IOException{

        ServerSocket s = new ServerSocket(8189);
        while (true){
            Socket incoming = s.accept();

            try {
                InputStream inStream = incoming.getInputStream();
                OutputStream outStream = incoming.getOutputStream();

                Scanner in = new Scanner(inStream);
                PrintWriter out = new PrintWriter(outStream, true);

                out.println("Hello! Enter BYE to exit");

                boolean done = false;

                while(!done && in.hasNextLine()){
                    String line = in.nextLine();

                    out.println("Echo: " + line);
                    if(line.trim().equals("BYE")) done=true;
                }


            }finally {
                incoming.close();
            }

        }

    }
}
```

这样的服务器每次只能处理一个客户端的请求. 完全没有展示出服务器的性能.
参考 <Java核心技术>