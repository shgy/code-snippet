前面学习了jsp和json两种view的配置方法. 其中jsp是默认的方式. json是api开发会用到的方式.
当然后面也有velocity, freemarker, xml等view的呈现方式. 这些都只是功能上的扩展, 不影响整体的架构.

所谓日拱一卒, 能够使用这两种ViewResolver后, 可以开始探究ViewResolver背后的实现机制, 最好的突破点就是自己实现一个ViewResolver.

自定义ViewResolver, 需要实现View接口, 并复写其中的两个方法:
```
	/**
	 * Return the content type of the view, if predetermined.
	 * <p>Can be used to check the content type upfront,
	 * before the actual rendering process.
	 * @return the content type String (optionally including a character set),
	 * or {@code null} if not predetermined.
	 */
	String getContentType();

	/**
	 * Render the view given the specified model.
	 * <p>The first step will be preparing the request: In the JSP case,
	 * this would mean setting model objects as request attributes.
	 * The second step will be the actual rendering of the view,
	 * for example including the JSP via a RequestDispatcher.
	 * @param model Map with name Strings as keys and corresponding model
	 * objects as values (Map can also be {@code null} in case of empty model)
	 * @param request current HTTP request
	 * @param response HTTP response we are building
	 * @throws Exception if rendering failed
	 */
	void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception;
```

自定义CustomerViewResolver类, 实现如下:
```
package com.springapp.mvc;

/**
 * Created by shgy on 18-3-13.
 */
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;

@Component("shgy")
public class CustomViewResolver implements View{

    public String getContentType() {
        return "text/html";
    }

    public void render(Map<String, ?> model, HttpServletRequest request,
                       HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        response.getWriter().print("Hello World CustomViewResolver");
    }

}
```

Controller的代码如下:

```
@Controller
public class HelloController {
	@RequestMapping(name = "/", method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {
		model.addAttribute("message", "Hello world!");
		return "shgy";
	}
}
```


mvc-dispatcher-servlet.xml的配置如下:
```
    <context:component-scan base-package="com.springapp.mvc"/>
    <!-- 配置注解驱动 -->
    <mvc:annotation-driven />
    <bean class="org.springframework.web.servlet.view.BeanNameViewResolver">
        <!--<property name="order" value="666"></property>-->
    </bean>
```
BeanNameViewResolver这个配置少不了 .  接下来追踪一下整个逻辑的调用过程.


