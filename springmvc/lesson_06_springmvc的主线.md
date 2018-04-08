1. DispatcherServlet.doService()
   Servlet的入口, 这个是公共的约定   

2. DispatcherServlet.doDispatch()
   请求的分发操作   

3. DispatcherServlet.getHandler()
   获取到处理当前url的Controller及方法

3. DispathcerServlet.getHandlerAdapter()
   通过handler得到Handler的适配器

4. AbstractHandlerMethodAdapter.handle() 
   使用HandlerAdapter()处理请求.

5. RequestMappingHandlerAdapter.handleInternal()
   
   ...

6. DispatcherServlet.render()
   开始渲染执行的结果, 这里会调用ViewResolver的render()方法.这个很关键.

在lesson_05中有两个方法, 一个是`render()`, 另一个是`getContentType()`.
render在这里就用到了.



https://blog.csdn.net/HermaeuxMora/article/details/51821332
