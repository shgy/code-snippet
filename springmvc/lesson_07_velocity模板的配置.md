velocity是Java开发中常见的模板, dubbo用的就是velocity, 但是dubbo用的是阿里的webx框架. 

使用velocity的方式也非常easy. 

首先应用相关的jar包, pom.xml的配置如下: 
```

    <properties>
        <spring.version>4.1.1.RELEASE</spring.version>
        <jackson.version>2.7.5</jackson.version>
        <velocity.version>1.7</velocity.version>
        <velocity-tool.version>2.0</velocity-tool.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
            <version>${spring.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>servlet-api</artifactId>
            <version>2.5</version>
        </dependency>

        <dependency>
            <groupId>javax.servlet.jsp</groupId>
            <artifactId>jsp-api</artifactId>
            <version>2.1</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>${spring.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity</artifactId>
            <version>${velocity.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.velocity</groupId>
            <artifactId>velocity-tools</artifactId>
            <version>${velocity-tool.version}</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.11</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

```

然后就是mvc-dispatcher-servlet.xml的配置
```
    <context:component-scan base-package="com.springapp.mvc"/>
    <!-- 配置注解驱动 -->
    <mvc:annotation-driven />
    <!-- 配置视图解析器 -->
    <!--velocity模板配置-->
    <bean id="velocityConfig" class="org.springframework.web.servlet.view.velocity.VelocityConfigurer">
        <property name="resourceLoaderPath" value="/WEB-INF/views/"/>
        <property name="configLocation" value="classpath:velocity.properties"/>
        <property name="velocityProperties">
            <props>
                <prop key="input.encoding">UTF-8</prop>
                <prop key="output.encoding">UTF-8</prop>
            </props>
        </property>
    </bean>
    
    <!--VelocityLayoutViewResolver-->
    <bean id="viewResolver" class="org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver">
        <property name="suffix" value=".vm"/>
        <property name="prefix" value=""/>
        <property name="contentType" value="text/html;charset=UTF-8"/>
        <property name="layoutUrl" value="layout/layout.vm"/>
    </bean>
```
接下来就是编写velocity的前端代码:
```
$ tree webapp
webapp
└── WEB-INF
    ├── mvc-dispatcher-servlet.xml
    ├── views
    │   ├── hello.jsp
    │   ├── index.vm
    │   └── layout
    │       └── layout.vm
    └── web.xml

$ cat webapp/WEB-INF/views/index.vm 
<h2>${msg}</h2>
$ cat webapp/WEB-INF/views/layout/layout.vm 
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

</head>
<body>
<div>header</div>
<div>
    $screen_content
</div>
<div>footer</div>
</body>

</html>

```
接下来就是准备Java的Controller代码:
```
package com.springapp.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class HelloController {
	@RequestMapping(name = "/", method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {
		model.addAttribute("msg", "Hello world!");
		return "index";
	}
}
```
同时, 准备velocity.properties文件, 默认为空即可.项目的整个目录结构如下:
```
$ tree src
src
├── main
│   ├── java
│   │   └── com
│   │       └── springapp
│   │           └── mvc
│   │               ├── config
│   │               ├── CustomViewResolver.java
│   │               ├── HelloController.java
│   │               ├── JSONController.java
│   │               └── model
│   │                   └── Shop.java
│   ├── resources
│   │   └── velocity.properties
│   └── webapp
│       └── WEB-INF
│           ├── mvc-dispatcher-servlet.xml
│           ├── views
│           │   ├── hello.jsp
│           │   ├── index.vm
│           │   └── layout
│           │       └── layout.vm
│           └── web.xml
└── test
    └── java
        └── com
            └── springapp
                └── mvc
                    └── AppTests.java
```


