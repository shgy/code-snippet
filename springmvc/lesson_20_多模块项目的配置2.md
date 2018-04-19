前面记录了spring多模块的配置,但是没有实现, 今天处理一下. 由于这些系列全部是框架型的东西. 没有业务的代码.
所以都是step by step的东西, 这个记录也就是起着操作手册的作用.

多模块的设置, 本身是maven的功能, 跟spring其实是没有关系的. 所以, 我们的关注点在pom.xml上.
在本例中, 我们设置了`springdemo-web`和`springdemo-test`两个模块. 其结构如下:
```
$ tree .
.
├── pom.xml
├── springdemo-test
│   ├── pom.xml
│   ├── springdemo-test.iml
│   └── src
│       └── test
│           └── java
│               └── com
│                   └── springapp
│                       ├── mvc
│                       │   ├── AppTests.java
│                       │   └── CaseInsensitiveComparatorTest.java
│                       └── mybatis
│                           └── mappers
│                               └── UserMapperTest.java
├── springdemo-web
│   ├── pom.xml
│   ├── springdemo-web.iml
│   ├── src
│   │   └── main
│   │       ├── java
│   │       │   └── com
│   │       │       └── springapp
│   │       │           ├── mvc
│   │       │           │   ├── CaseInsensitiveComparator.java
│   │       │           │   └── HelloController.java
│   │       │           └── mybatis
│   │       │               ├── entities
│   │       │               │   └── User.java
│   │       │               └── mappers
│   │       │                   └── UserMapper.java
│   │       ├── resources
│   │       │   ├── jdbc.properties
│   │       │   ├── mappers
│   │       │   │   └── UserMapper.xml
│   │       │   ├── mvc-dispatcher-servlet.xml
│   │       │   └── mybatis.sql
│   │       └── webapp
│   │           └── WEB-INF
│   │               ├── pages
│   │               │   └── hello.jsp
│   │               └── web.xml
│   └── web
│       ├── index.jsp
│       └── WEB-INF
│           ├── applicationContext.xml
│           ├── dispatcher-servlet.xml
│           └── web.xml
└── springmvc-demo-01.iml

26 directories, 23 files

```
这里面代码都是前面说过的, 这里就不赘述了. 整个项目一共有3个pom文件.
```
$ cat springmvc-demo-01/pom.xml 
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.springapp</groupId>
    <artifactId>springmvc-demo-01</artifactId>
    <packaging>pom</packaging>
    <version>1.0-SNAPSHOT</version>

    <modules>
        <module>springdemo-web</module>
        <module>springdemo-test</module>
    </modules>

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

        <!--Spring框架核心库 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>${spring.version}</version>
        </dependency>

        <!--Spring java数据库访问包，在本例中主要用于提供数据源 -->
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
        <!-- aspectJ AOP 织入器 -->
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.8.9</version>
        </dependency>


        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.0.1</version>
            <scope>provided</scope>
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

$ cat springmvc-demo-01/springdemo-web/pom.xml 
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.springapp</groupId>
        <artifactId>springmvc-demo-01</artifactId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <groupId>com.springapp</groupId>
    <artifactId>sprindemo-web</artifactId>
    <packaging>war</packaging>
</project>


$ cat springmvc-demo-01/springdemo-test/pom.xml 
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>springmvc-demo-01</artifactId>
        <groupId>com.springapp</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springdemo-test</artifactId>

    <name>springdemo-test</name>
    <!-- FIXME change it to the project's website -->
    <url>http://www.example.com</url>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.7</maven.compiler.source>
        <maven.compiler.target>1.7</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.springapp</groupId>
            <artifactId>sprindemo-web</artifactId>
            <version>1.0-SNAPSHOT</version>
            <classifier>classes</classifier>
            <scope>test</scope> 
       </dependency>
    </dependencies>
</project>

```

这里需要注意的是: test以来springdemo-web项目时, 依赖要写成
```
    <dependencies>
        <dependency>
            <groupId>com.springapp</groupId>
            <artifactId>sprindemo-web</artifactId>
            <version>1.0-SNAPSHOT</version>
            <classifier>classes</classifier>
            <scope>test</scope>
        </dependency>
    </dependencies>
```
不然, 在整个项目中执行测试是会报错, 找不到相关的类. 因为war包是没法引入相关的类. 

参考:
https://pragmaticintegrator.wordpress.com/2010/10/22/using-a-war-module-as-dependency-in-maven/
