spring的测试非常简单, 毕竟这个框架这么多年, 打磨得已经很好了. 
springmvc已经帮我们把测试的框架搭建起来了.
```
$ tree src/test
src/test
└── java
    └── com
        └── springapp
            └── mvc
                ├── AppTests.java
                └── CaseInsensitiveComparatorTest.java

4 directories, 2 files

```
AppTests的代码如下:
```
package com.springapp.mvc;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:mvc-dispatcher-servlet.xml")
public class AppTests {
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void simple() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("json"));
    }
}
``
这个是测试controller层, 其实一般这一层都不太需要测试, 要测试Service怎么办呢? 一样画葫芦
```
package com.springapp.mvc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

/**
 * Created by shgy on 18-4-15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mvc-dispatcher-servlet.xml")
public class CaseInsensitiveComparatorTest {

    @Autowired
    private Comparator<String> service;

    @Test
    public void testCompare(){

        int result = service.compare("aa","bb");
        assertEquals(result,-1);
    }
}

```

这里有一个问题: 如果我们有多个配置文件, `ContextConfiguration`注释如何处理?
```
@ContextConfiguration(locations={"first.xml", "second.xml"})
```


参考:
https://www.ibm.com/developerworks/cn/java/j-lo-springunitest/index.html
https://stackoverflow.com/questions/12042834/spring-and-testing-with-testcontextframework?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
