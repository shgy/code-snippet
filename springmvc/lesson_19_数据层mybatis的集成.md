MyBatis跟Spring的集成也是很容易的. 而且可以去除MyBatis中很多不必要的配置.
依然是3步走

Step1: 准备数据源MySQL.
```
create database if not exists mybatis default charset utf8 collate utf8_general_ci;

create table if not exists user(
  id int primary key auto_increment,
  name varchar(50)
);
insert into user (name) values('hello');
```


Step2: 配置maven, 引入相关的jar包.
```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.springapp</groupId>
    <artifactId>springmvc-demo-01</artifactId>
    <packaging>war</packaging>
    <version>1.0-SNAPSHOT</version>
    <name>springmvc-demo-01</name>

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
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>${spring.version}</version>
        </dependency>


        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>${spring.version}</version>
            <scope>test</scope>
        </dependency>
        
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.8.9</version>
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
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.11</version>
            <scope>test</scope>
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
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.2.2</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.26</version>
        </dependency>
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>1.3.2</version>
        </dependency>
    </dependencies>

    <build>
        <finalName>springmvc-demo-01</finalName>
        <plugins>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.6</source>
                    <target>1.6</target>
                </configuration>
            </plugin>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <includes>
                        <include>**/*Tests.java</include>
                    </includes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```
这其中, 最重要的是
```
       <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>1.3.2</version>
       </dependency>

```

Step3: 配置spring相关bean
```
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="com.springapp"/>
    <context:property-placeholder location="classpath:jdbc.properties"/>
    <!-- 配置视图解析器 -->
    <bean name="json" class="org.springframework.web.servlet.view.json.MappingJackson2JsonView">
        <property name="prettyPrint" value="true"/>
    </bean>
    <bean name="viewResolver" class="org.springframework.web.servlet.view.BeanNameViewResolver"></bean>
    <!--<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">-->
        <!--<property name="prefix" value="/WEB-INF/pages/"/>-->
        <!--<property name="suffix" value=".jsp"/>-->
    <!--</bean>-->
    <!--创建一个sql会话工厂bean，指定数据源 -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!-- 1指定数据源 -->
        <property name="dataSource" ref="dataSource" />
        <!-- 2类型别名包，默认引入com.zhangguo.Spring61.entities下的所有类 -->
        <property name="typeAliasesPackage" value="com.springapp.mybatis.entities"></property>
        <!-- 3指定sql映射xml文件的路径 -->
        <property name="mapperLocations"
                  value="classpath:mappers/*Mapper.xml"></property>
    </bean>
    <!--自动扫描映射接口-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!-- 指定sql会话工厂，在上面配置过的 -->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"></property>
        <!-- 指定基础包，即自动扫描com.zhangguo.Spring61.mapping这个包以及它的子包下的所有映射接口类 -->
        <property name="basePackage" value="com.springapp.mybatis.mappers"></property>
    </bean>
    <context:property-placeholder location="classpath:jdbc.properties"/>
    <bean id="dataSource"
          class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="${jdbc.driver}"/>
        <property name="username" value="${jdbc.username}"/>
        <property name="password" value="${jdbc.password}"/>
        <property name="url" value="${jdbc.url}"/>
    </bean>
</beans>

$cat jdbc.properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3305/mybatis
jdbc.username=root
jdbc.password=root123

$ cat mappers/UserMapper.xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.springapp.mybatis.mappers.UserMapper">
  <select id="findUserById" parameterType="Integer" resultType="User">
    select * from user where id=#{id}
  </select>
</mapper>  
``` 
整个resources目录结构如下:
```
$ tree resources
resources/
├── jdbc.properties
├── mappers
│   └── UserMapper.xml
├── mvc-dispatcher-servlet.xml
└── mybatis.sql

```
mybatis.sql即为最开始准备数据源的sql, 在这里没有什么作用.
接下来就是Java代码的结构了.

Step4 工程端实现:
```
$ tree java/
java/
└── com
    └── springapp
        ├── mvc
        │   └── HelloController.java
        └── mybatis
            ├── entities
            │   └── User.java
            └── mappers
                └── UserMapper.java

```
其中, 各个类的代码如下:
```
$ cat java/com/springapp/mybatis/entities/User.java 
package com.springapp.mybatis.entities;

/**
 * Created by shgy on 18-4-15.
 */
import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable{

    private Integer id;
    private String name;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public User(Integer id, String password) {
        super();
        this.id = id;
        this.name = password;
    }
    public User() {
        super();
        // TODO Auto-generated constructor stub
    }
}

```
这个非常简单.
```
$ cat java/com/springapp/mybatis/mappers/UserMapper.java 
package com.springapp.mybatis.mappers;

import com.springapp.mybatis.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Created by shgy on 18-4-15.
 */

@Repository
public interface UserMapper {
    User findUserById(Integer id);
}
```
也相当简单,一个接口而已.

```
$ cat java/com/springapp/mvc/HelloController.java 
package com.springapp.mvc;

import com.springapp.mybatis.entities.User;
import com.springapp.mybatis.mappers.UserMapper;
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
        private UserMapper userMapper;

        @RequestMapping(method = RequestMethod.GET)
        public String printWelcome(ModelMap model) {
                model.addAttribute("message", "Hello world!");
                return "json";
        }

        @RequestMapping(value="/mybatis",method = RequestMethod.GET)
        public String mybatis(@RequestParam("userId") Integer userId, Model model){

                User user = userMapper.findUserById(userId);
                model.addAttribute("user",user);
                return "json";
        }

}
```
也是很简单. 但是一般不是这样用. 即controller不直接控制Mapper. 中间应该有一层service

测试代码也很简单:
```
package com.springapp.mybatis.mappers;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by shgy on 18-4-18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mvc-dispatcher-servlet.xml")
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testFindUserById(){
        Assert.assertEquals(userMapper.findUserById(1).getName(),"hello");
    }
}

```


综上, 学会框架的使用, 除了架构的清晰, 代码量也会减小很多. 让人专注到业务, 而不用操心结构的管理.

springmvc + spring + mybatis的集成, 核心在mvc-dispatcher-servlet.xml中bean的配置.回顾一下, 还是很简单的.




参考:
https://blog.csdn.net/eson_15/article/details/51684968
http://www.cnblogs.com/best/p/5638827.html
