在JavaWeb开发中，Filter也是必备组件之一。相当多的框架， 比如Struts Spring Guice等都是通过
Filter这一入口来实现自己的处理思想。

Filter一般用于过滤拦截， 比如jetty的DoSFilter，用于防止Dos攻击。 下面给个简单的demo吧。

```
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.*;
import java.io.IOException;
import java.util.EnumSet;

/**
 * Created by shgy on 17-8-10.
 */
public class FilterDemo {
    public static void main(String[] args) throws Exception {

        final ServletContextHandler context =
                new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase("webapp");
        context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "true");

        context.addServlet(DefaultServlet.class, "/");

        context.addFilter(MyFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));

        final Server server = new Server(8090);
        server.setHandler(context);

        server.start();
        server.join();
    }

    public static class MyFilter implements Filter
    {

        public void init(FilterConfig filterConfig) throws ServletException {
            System.out.println("init filter");
        }

        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            System.out.println("do Filter");
            chain.doFilter(request, response);
        }

        public void destroy() {
            System.out.println("destory");
        }
    }
}


```
