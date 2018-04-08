以前使用django开发过web应用, 现在切换到了springmvc, 总是希望有个类比. 
django中, app是一个个功能单元的模块. 在springmvc中, 对应着一个Controller. 

比如登录模块, 对应的Controller如下:
```
package com.springapp.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/login")
public class LoginController {

	@RequestMapping(method=RequestMethod.GET)
	public String index(ModelMap model) {
		model.addAttribute("msg", "Hello world!");
		return "login";
	}
}
```

比如首页模块:
```
package com.springapp.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class IndexController {

	@RequestMapping(method=RequestMethod.GET)
	public String index(ModelMap model) {
		model.addAttribute("msg", "Hello world!");
		return "index";
	}
}
```

这其实跟django的配置有些像了. 只不过, django拆得跟细致, 拆出了一个urls.py单独的配置文件而已.

这里隐藏了一个规则: `The path in @RequestMapping on the method is relative to the path on the class annotation.`
也就是说, 对于RequestMapping注解来说, method上的路径是基于类上的路径.比如: `localhost:8080/login`, 类上的路径是`/login`,
方法上的路径是`""`(空串). 


在浏览器中输入路径, 就能使用相应的Controller方法来处理业务逻辑. 这个过程是怎样的?

springmvc的入口是`DispatcherServlet.doService()`方法. 该方法中, 有很重要的一句代码`mappedHandler = getHandler(processedRequest); //line 916`

1. 根据request寻找对应的`HandlerMethod`
1.1 在AbstractHandlerMethodMapping的urlMap中直接查询
1.2 根据url支持的各种规则, 比如通配, 正则, TrailingSlash等规则获取匹配的HandlerMethod

2. 对所有的HandlerMethod进行排序, 排序的key是mvc-dispathcher-servlet.xml中配置的order顺序.

3. 返回最优的那个, 如果优先级相同的右多个, 则抛出异常.

这就是jsp页面的寻路规则, 对应着`RequestMappingHandlerMapping`, 还有BeanNameUrlHandlerMapping, 这个后面再说.


接下来, 再深入一层: RequestMappingHandlerMapping是如何构建出来的? 前面是理解了其行为方式, 这里才开始追本溯源.

由于这些url都是在代码中使用注解配置出来的, viewresolver是配置在mvc-dispatcher-servlet.xml文件中. 那么可以很容易的猜测到:
springmvc会解析xml和annotation, 获取相关的信息.  

问题:  springmvc的RequestMapping配置
```
@Controller
@RequestMapping(name="/")
public class IndexController {

	@RequestMapping(method=RequestMethod.GET)
	public String index(ModelMap model) {
		model.addAttribute("msg", "Hello world!");
		return "index";
	}
}
```
和
```
@Controller
public class IndexController {

	@RequestMapping(name="/", method=RequestMethod.GET)
	public String index(ModelMap model) {
		model.addAttribute("msg", "Hello world!");
		return "index";
	}
}
```
这两种配置有什么区别?

从功能上讲, 只在方法上设置RequestMapping是不对的. 这个设置, 会导致输入任何的url, 如`localhost:8080/asdf`都会跳转到index.

为什么会这样呢?

如果同时在类和方法上设置RequestMapping, 两者会融合. 融合的代码如下:
``` RequestMappingInfo.combine()方法
	/**
	 * Combines "this" request mapping info (i.e. the current instance) with another request mapping info instance.
	 * <p>Example: combine type- and method-level request mappings.
	 * @return a new request mapping info instance; never {@code null}
	 */
	@Override
	public RequestMappingInfo combine(RequestMappingInfo other) {
		String name = combineNames(other);
		PatternsRequestCondition patterns = this.patternsCondition.combine(other.patternsCondition);
		RequestMethodsRequestCondition methods = this.methodsCondition.combine(other.methodsCondition);
		ParamsRequestCondition params = this.paramsCondition.combine(other.paramsCondition);
		HeadersRequestCondition headers = this.headersCondition.combine(other.headersCondition);
		ConsumesRequestCondition consumes = this.consumesCondition.combine(other.consumesCondition);
		ProducesRequestCondition produces = this.producesCondition.combine(other.producesCondition);
		RequestConditionHolder custom = this.customConditionHolder.combine(other.customConditionHolder);

		return new RequestMappingInfo(name, patterns,
				methods, params, headers, consumes, produces, custom.getCondition());
	}
```
融合的时间点就是项目启动初始化的阶段
```
   //--- RequestMappingHandlerMapping.getMappingForMethod()

	protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
		RequestMappingInfo info = null;
		RequestMapping methodAnnotation = AnnotationUtils.findAnnotation(method, RequestMapping.class);
		if (methodAnnotation != null) {
			RequestCondition<?> methodCondition = getCustomMethodCondition(method);
			info = createRequestMappingInfo(methodAnnotation, methodCondition);
			RequestMapping typeAnnotation = AnnotationUtils.findAnnotation(handlerType, RequestMapping.class);
			if (typeAnnotation != null) {
				RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);
				info = createRequestMappingInfo(typeAnnotation, typeCondition).combine(info);
			}
		}
		return info;
```

融合的关键在于`PatternsRequestCondition patterns = this.patternsCondition.combine(other.patternsCondition);`
在这个方法的代码很简单, 关键在于`result.add("");`. 就是说, 如果两个RequestMapping的pattern都是`[]`, 那么融合后,就不是空的了,
而是[""]. 就是这么一个细微的区别, 导致了上面两种配置方式, 呈现出了完全不同的结果.
```
	/**
	 * Returns a new instance with URL patterns from the current instance ("this") and
	 * the "other" instance as follows:
	 * <ul>
	 * <li>If there are patterns in both instances, combine the patterns in "this" with
	 * the patterns in "other" using {@link PathMatcher#combine(String, String)}.
	 * <li>If only one instance has patterns, use them.
	 * <li>If neither instance has patterns, use an empty String (i.e. "").
	 * </ul>
	 */
	@Override
	public PatternsRequestCondition combine(PatternsRequestCondition other) {
		Set<String> result = new LinkedHashSet<String>();
		if (!this.patterns.isEmpty() && !other.patterns.isEmpty()) {
			for (String pattern1 : this.patterns) {
				for (String pattern2 : other.patterns) {
					result.add(this.pathMatcher.combine(pattern1, pattern2));
				}
			}
		}
		else if (!this.patterns.isEmpty()) {
			result.addAll(this.patterns);
		}
		else if (!other.patterns.isEmpty()) {
			result.addAll(other.patterns);
		}
		else {
			result.add("");
		}
		return new PatternsRequestCondition(result, this.pathHelper, this.pathMatcher, this.useSuffixPatternMatch,
				this.useTrailingSlashMatch, this.fileExtensions);
	}
```












 



