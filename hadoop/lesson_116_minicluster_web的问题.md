启动MiniYarnCluster之后, 在浏览器中访问，RM的webapp会自动显示成NM的webapp. 设置只启动RM， 则webapp显示正常。
那么这肯定是webapp的问题， 有可能是单例造成的。 本文的目的就是追寻这个问题的原因。
在启动的过程中, 有这样一个warning信息。
```
Jul 17, 2017 12:16:30 PM com.google.inject.servlet.GuiceFilter setPipeline
WARNING: Multiple Servlet injectors detected. This is a warning indicating that you have more than one GuiceFilter running in your web application. 
If this is deliberate, you may safely ignore this message. If this is NOT deliberate however, your application may not work as expected.

```

1. 学习使用guice servlet开发web应用。
https://github.com/google/guice/wiki/Servlets

复现这个warning的demo

```
package shgy.guice;

import com.google.inject.Guice;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.ServletModule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;

class RMWebapp
{
    @Singleton
    static class MyServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            response.getWriter().print("RMWeb app Hello!\nYour path is: " + request.getServletPath());
        }
    }

    class WebApp extends ServletModule {
        @Override
        protected void configureServlets() {

            serve("/guice").with(MyServlet.class);
        }
    }

    public void start() throws Exception {
        Guice.createInjector(new WebApp());
        Server server = new Server(8080);
        ServletContextHandler handler =
                new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        handler.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        handler.addServlet(DefaultServlet.class, "/");
        server.start();
    }
}

class NMWebapp
{
    @Singleton
    static class MyServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            response.getWriter().print("NMWeb app Hello!\nYour path is: " + request.getServletPath());
        }
    }

    class WebApp extends ServletModule {
        @Override
        protected void configureServlets() {

            serve("/guice").with(MyServlet.class);
        }
    }

    public void start() throws Exception {
        Guice.createInjector(new WebApp());
        Server server = new Server(8081);
        ServletContextHandler handler =
                new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);

        handler.addFilter(GuiceFilter.class, "/*", EnumSet.allOf(DispatcherType.class));
        handler.addServlet(DefaultServlet.class, "/");
        server.start();
    }
}

public class MyMain {
    public static void main(String... args) throws Exception {
        new RMWebapp().start();
        new NMWebapp().start();
    }
}

```

stackoverflow上也有说明如何解决这个问题
https://stackoverflow.com/questions/9074704/embedded-jetty-different-ports-for-internally-and-externally-visible-endpoints


