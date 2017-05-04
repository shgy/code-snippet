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
 * 多线程线程的服务器, 每个客户端一个线程
 *
 */

public class SimpleServer2 {

    public static void main(String[] args) throws IOException{

        ServerSocket s = new ServerSocket(8189);
        while (true){
            Socket incoming = s.accept();

            new Thread(new ThreadedEchoHandler(incoming)).start();
        }

    }
}

class ThreadedEchoHandler implements Runnable{
    private Socket s;

    public ThreadedEchoHandler(Socket s){
        this.s = s;
    }
    @Override
    public void run(){

        try {
            InputStream inStream = s.getInputStream();
            OutputStream outStream = s.getOutputStream();
            Scanner in = new Scanner(inStream);
            PrintWriter out = new PrintWriter(outStream, true);

            out.println("Hello! Enter BYE to exit");

            boolean done = false;

            while(!done && in.hasNextLine()){
                String line = in.nextLine();

                out.println("Echo: " + line);
                if(line.trim().equals("BYE")) done=true;
            }


        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                this.s.close();
            } catch (IOException e) {}
        }

    }
}
```



参考 <Java核心技术>