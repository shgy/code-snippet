前面花了比较大的篇幅了解springmvc的view层的东西. 碎片式的学习, 碎片式的记录. 

接下来开始了解service层的接入:
为了简化跟聚焦, 这里只提供api接口, 而没有界面的开发.
开发一个api接口compare, 传入两个参数: input1和input2, 输出结果为一段描述性文字,谁比谁大就好了.
```
package com.springapp.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;

@Controller
@RequestMapping("/")
public class HelloController {

	@Autowired
	Comparator<String> comparator;

	@RequestMapping(method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {
		model.addAttribute("message", "Hello world!");
		return "json";
	}

	@RequestMapping(value = "/compare", method = RequestMethod.GET)
	public String compare(@RequestParam("input1") String input1,
						  @RequestParam("input2") String input2, Model model) {

		int result = comparator.compare(input1, input2);
		String inEnglish = (result < 0) ? "less than" : (result > 0 ? "greater than" : "equal to");

		String output = "According to our Comparator, '" + input1 + "' is " + inEnglish + "'" + input2 + "'";

		model.addAttribute("output", output);
		return "json";
	}
}

```
这里使用`Autowired`注解.
```
package com.springapp.mvc;

import org.springframework.stereotype.Service;

import java.util.Comparator;

/**
 * Created by shgy on 18-4-15.
 */

@Service
public class CaseInsensitiveComparator implements Comparator<String> {
    @Override
    public int compare(String s1, String s2) {
        assert s1 != null && s2 != null;
        return String.CASE_INSENSITIVE_ORDER.compare(s1, s2);
    }
}

```

有了service层, 就可以开始更多的业务逻辑的开发. 接下来还是聚焦在Service层, 了解Spring的测试工具.
测试的必要性, 毋庸赘述. 待测试功能学习完毕后, 开始学习在spring中集成mybatis.




参考:
https://spring.io/blog/2011/01/04/green-beans-getting-started-with-spring-mvc/
