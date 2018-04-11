spring默认会在/WEB-INF/目录下寻找规则为`servlet-name`+"servlet.xml"的文件作为启动配置文件.
这里的核心方法就是`XmlWebApplicationContext.loadBeanDefinitions()`方法.

如果希望更改这一规则, 有那些办法呢?

1. 添加参数
```
<init-param>
	<param-name>contextConfigLocation</param-name>
	<param-value>classpath:mvc-dispatcher-servlet.xml</param-value>
</init-param>

```

2. 自定义ApplicationContext类, 实现xml文件的加载..


