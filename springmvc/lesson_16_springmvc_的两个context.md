springmvc启动了两个context, applicationContext和WebApplicationContext.
分别对应着两个配置文件: `applicationContext.xml`, `dispatcher-servlet.xml`
在web.xml中的配置如下:
```
<listener>  
  <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>  
</listener>  
<context-param>  
 <param-name>contextConfigLocation</param-name>  
 <param-value>classpath*:conf/applicationContext*.xml</param-value>  
</context-param>  

<servlet>
      <servlet-name>mvcServlet</servlet-name>
      <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
      <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:conf/spring-dispatcher-servlet.xml</param-value>
      </init-param>
      <load-on-startup>1</load-on-startup>
</servlet>

```
