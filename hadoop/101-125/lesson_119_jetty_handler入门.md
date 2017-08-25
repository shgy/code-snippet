其实， 这部分的内容已经偏离了hadoop-yarn的知识点。 花时间学习jetty的原因有如下两点：
1. hadoop中的web服务大多都是基于jetty
2. jetty的嵌入式web思想很好： 
   "Don’t deploy your application in Jetty, deploy Jetty in your application!" 
3. jetty远比tomcat轻量级，


jetty的架构中有两个核心点： connector和handler. connector定制化的需求很少， 虽然是决定jetty性能的核心，
但是已经很成熟了， 而且偏底层。 因此先从handler入手。

jetty的helloworld就是一个handler, 通过handle方法实现了输出hello world的功能，非常直观。

jetty各种复杂的功能基本上是通过handler实现的。 先学习最基础4种。

1. HandlerCollection  
   通过代码可知， 他按照顺序调用handler, 在处理的过程中忽略异常(除了IOException和RuntimeException)
   适用于处理统计 和 日志类的需求。
2. HandlerList
   通过代码可知， 他按照顺序调用handler, 在处理的过程中受异常和上一个handler执行结果的影响`baseRequest.isHandled()`。
3. HandlerWrapper 
   装饰器 (暂时没有弄明白其作用)
4. ContextHandlerCollection
   上下文处理器。
   Multiple contexts may have the same context path and they are called in order until one handles the request.
   (暂时没有弄明白其作用)


