感觉DefaultServlet的功能跟ResourceHandler差不多，都是处理静态网页。

```
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Created by shgy on 17-8-10.
 */
public class DefaultServletDemo {
    public static void main(String[] args) throws Exception {

        final ServletContextHandler context =
                new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase("webapp");
        // 设置DefaultServlet的初始化参数
        context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "true");

        context.addServlet(DefaultServlet.class, "/");

        final Server server = new Server(8090);
        server.setHandler(context);

        server.start();
        server.join();
    }
}

```