通常在web应用中, 我们需要有样式的页面和数据api并存.  web页面提供管理功能, api提供数据服务.
spring对此提供了很好的支持: ContentNegotiatingViewResolver, 内容协商解析器, 本质上它是一个代理, 根据url类型做了一次转发操作.
mvc-dispatcher-servlet.xml的配置如下:
```
    <context:component-scan base-package="com.springapp.mvc"/>
    <!-- 配置注解驱动 -->
    <mvc:annotation-driven />
    <!-- 配置视图解析器 -->
    <bean class="org.springframework.web.servlet.view.ContentNegotiatingViewResolver">
        <property name="viewResolvers">
            <list>
                <bean class="org.springframework.web.servlet.view.BeanNameViewResolver"/>
                <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
                    <property name="prefix"	value="/WEB-INF/views/"/>
                    <property name="suffix"	value=".jsp"/>
                </bean>
            </list>
        </property>
        <property name="defaultViews">
            <list>
                <bean class="org.springframework.web.servlet.view.json.MappingJackson2JsonView"/>
            </list>
        </property>
    </bean>
```

controller的代码如下:
```
@Controller
public class HelloController {
	@RequestMapping(name = "/nego", method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {
		model.addAttribute("msg", "Hello world!");
		return "hello";
	}
}
```
在浏览器中输入`localhost:8080/nego/`则会显示hello.jsp页面, 输入`hlocalhost:8080/nego.json`则会输出json数据.
