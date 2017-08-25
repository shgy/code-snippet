Servlet可以说是JavaWeb的基石。 使用jetty 开发Servlet程序也相当简单， 想想Tomcat那复杂的一套，
jetty可谓相当小清新了。 写个简单的API啥的， 相当敏捷。

不说了， 弄个简单的demo吧。

```

public class ServletHandlerDemo {
    public static class MyServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            this.doPost(req, resp);
        }

        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {

            resp.getWriter().println("hello world");

        }
    }
    public static void main(String[] args) throws Exception {

        Server server = new Server(8090);

        ServletHandler sh = new ServletHandler();
        sh.addServletWithMapping(MyServlet.class,"/hello");
        server.setHandler(sh);
        server.start();
        server.join();
    }
}

```