在intellij环境下, 搭建springmvc的开发环境, 非常简单. 简单到不用写一行代码.

1. File -> New Project -> Spring MVC 即可.

2. 下载并解压Tomcat

3. Run -> Edit Configurations  -> 左侧的+号 -> 设置Tomcat的安装环境.

4. 还是当前页面, 第二个选项卡[Deployment], 中间的+号 --> Artifact

5. Apply 保存. 然后运行, 就可以看到浏览器界面的Hello World了.


有Strut2框架的开发经验, 再学SpringMVC, 就明白他们的入口都是Servlet, load-on-startup的Servlet.
后面就是相关框架的逻辑, Solr也是如此.


如果希望将spring的配置文件: mvc-dispatcher-servlet.xml放到其他的地方, 可以修改web.xml的配置
```
    <servlet>
		<servlet-name>mvc-dispatcher</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>classpath:mvc-dispatcher-servlet.xml</param-value>
		</init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
```
这里的classpath即指: WEB-INF/classes和WEB-INFO/lib目录.
