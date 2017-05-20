第一步, 添加maven依赖
```
        <dependency>
            <groupId>org.eclipse.jetty.aggregate</groupId>
            <artifactId>jetty-all</artifactId>
            <version>9.2.12.M0</version>
        </dependency>
```
第二步, Java代码
```
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by shgy on 17-5-5.
 */
public class JettyHelloworld extends AbstractHandler
{
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<h1>Hello World</h1>");
    }

    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);
        server.setHandler(new JettyHelloworld());
        server.start();
        server.join();
    }
}
```
