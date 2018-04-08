springmvc配置json相当简单. 简单到令人发指.
step 1: mvn的pom.mxl配置
```
 <properties>
        <spring.version>4.1.1.RELEASE</spring.version>
        <jackson.version>2.7.5</jackson.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
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
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.11</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

```
这里最重要的是jackson的3个包.

step 2: mvc-dispatcher-servlet.xml的配置
```
    <context:component-scan base-package="com.springapp.mvc"/>
    <!-- 配置注解驱动 -->
    <mvc:annotation-driven />
    <!-- 配置视图解析器 -->
    <bean name="json" class="org.springframework.web.servlet.view.json.MappingJackson2JsonView">
        <property name="prettyPrint" value="true"/>
    </bean>
    <bean name="viewResolver" class="org.springframework.web.servlet.view.BeanNameViewResolver"></bean>
```

这里配置使用beanName来解析渲染view

step3: Controller的开发
```
@Controller
public class HelloController {
	@RequestMapping(name = "/", method = RequestMethod.GET)
	public String printWelcome(ModelMap model) {
		model.addAttribute("message", "Hello world!");
		return "json";
	}
}
```
由于配置了beanNameViewResolver,所以返回值要跟beanName一致, 即`json`
