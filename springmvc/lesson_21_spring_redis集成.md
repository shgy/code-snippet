redis基本上是高并网站的标配了. 所以使用spring操作redis的重要性也是不言而喻的.

redis的用法一般分为两种:
 一种是作为缓存, 对用户透明. 比如数据本来在MySQL中, 有些复杂耗时耗CPU的操作得到的结果, 只用一次就丢弃, 太可惜了.
放到redis中, 实现重用以提高响应速度. 
一种是作为独立的数据库,对外开发接口实现业务功能.

先来学习redis跟spring的集成, 无论redis怎么用, 这一步都是必不可少的.

首先看spring-data-redis的官方文档`https://docs.spring.io/spring-data/data-redis/docs/1.7.0.RELEASE/reference/html/#requirements`
这个文档中有各个版本的描述, 这里重要的是依赖关系. 比如, 我用的spring的版本是`4.1.1.RELEASE`, 那么对应的spring-data-redis的版本是
多少呢? 可以用1.7.0 

```
Spring Data Redis 1.2.x binaries requires JDK level 6.0 and above, and Spring Framework 3.2.8 and above.

In terms of key value stores, Redis 2.6.x or higher is required. Spring Data Redis is currently tested against the latest 2.6 and 2.8 releases.
```

整个moudel的结构如下:
```
$ tree springdemo-redis/
springdemo-redis/
├── pom.xml
├── springdemo-redis.iml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── springapp
    │   │           └── redis
    │   │               └── App.java
    │   └── resources
    │       └── META-INF
    │           └── spring
    │               ├── redis.properties
    │               └── spring-redis.xml
    └── test
        └── java
            └── com
                └── springapp
                    └── AppTest.java

```
有些是maven自动生成的, 比如test. 

step1: maven的依赖
```
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>springmvc-demo-01</artifactId>
        <groupId>com.springapp</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>springdemo-redis</artifactId>


    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.7</maven.compiler.source>
        <maven.compiler.target>1.7</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-redis</artifactId>
            <version>1.7.0.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>redis.clients</groupId>
            <artifactId>jedis</artifactId>
            <version>2.9.0</version>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.11</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

</project>

```
关于spring的依赖在整个project的依赖pom.xml文件中.这里不赘述.

Step2: spring的配置文件. 这里跟jdbc一样, 拆成properties和xml两个文件:
```
$ cat redis.properties 
#redis中心
#绑定的主机地址
redis.host=127.0.0.1 
#指定Redis监听端口，默认端口为6379
redis.port=6379 
#授权密码（本例子没有使用）
# redis.password=123456
#最大空闲数：空闲链接数大于maxIdle时，将进行回收
redis.maxIdle=100  
#最大连接数：能够同时建立的“最大链接个数”
redis.maxActive=300  
#最大等待时间：单位ms
redis.maxWait=1000   
#使用连接时，检测连接是否成功
redis.testOnBorrow=true 
#当客户端闲置多长时间后关闭连接，如果指定为0，表示关闭该功能
redis.timeout=10000

$ cat spring-redis.xml 
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd">

    <context:component-scan base-package="com.springapp"/>

    <context:property-placeholder location="classpath:META-INF/spring/redis.properties"/>


    <bean id="jedisConnectionFactory" class="org.springframework.data.redis.connection.jedis.JedisConnectionFactory">

        <property name="port" value="${redis.port}" />
        <property name="hostName" value="${redis.host}" />
    </bean>
    <!-- redis template definition -->
    <bean id="redisTemplate" class="org.springframework.data.redis.core.RedisTemplate"
          p:connection-factory-ref="jedisConnectionFactory"/>

</beans>

```
这里需要注意的是 命名空间`p`, 这个需要添加schema.

step 3 代码:
```
package com.springapp.redis;


import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;


public class App {
    
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:META-INF/spring/spring-redis.xml");
        RedisTemplate<String, String> redisTemplate = (RedisTemplate) context.getBean("redisTemplate");
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        operations.set("key2","hello2");
        System.out.println(operations.get("key2"));
    }
}
```
查看redis的客户端, 在redis中, key并不是`key2`, 而是有前缀
```
127.0.0.1:6379> keys "*"
1) "\xac\xed\x00\x05t\x00\x04key2"
2) "key1"
127.0.0.1:6379> 

```
这个是由spring-data-redis的序列化机制造成的. 







