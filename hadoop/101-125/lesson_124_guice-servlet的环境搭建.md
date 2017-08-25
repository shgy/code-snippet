由于minicluster_web的问题， 初步了解了一下jetty和guice. 其实guice在elasticsearch中也用到了。想来还是tomcat和spring太过于重量级了。

关于guice源码的编译， 还是使用`ant dist -Dversion=3.0`命令即可。虽然有maven, 但是使用的时候会报错，
也不取追究具体的原因了。毕竟目的在于hadoop这一套， 不能偏题太远。

由于希望用到guice的源码来定位问题(目前也没有解决问题，毕竟一天两天熟悉guice-servlet的设计思想，还是难了一些 ),
所以使用guice的core和servlet的源码来写demo, 目录结构如下：
```
├── pom.xml
├── src
│   ├── main
│   │   ├── guice-core
│   │   │   └── com
│   │   ├── guice-servlet
│   │   │   └── com
│   │   ├── java
│   │   │   ├── MyMain.java
│   │   └── resources
```
pom.xml的文件. guice用到了cglib和aopalliance包。
``` 

    <dependencies>

        <dependency>
            <groupId>org.eclipse.jetty.aggregate</groupId>
            <artifactId>jetty-all</artifactId>
            <version>9.2.12.M0</version>
        </dependency>
        <dependency>
            <groupId>org.eclipse.jetty.toolchain</groupId>
            <artifactId>jetty-test-helper</artifactId>
            <version>4.0</version>
        </dependency>
        <dependency>
            <groupId>cglib</groupId>
            <artifactId>cglib</artifactId>
            <version>2.2</version>
        </dependency>

        <dependency>
            <groupId>javax.inject</groupId>
            <artifactId>javax.inject</artifactId>
            <version>1</version>
        </dependency>

        <dependency>
            <groupId>aopalliance</groupId>
            <artifactId>aopalliance</artifactId>
            <version>1.0</version>
        </dependency>


    </dependencies>
    <profiles>
        <profile>
            <id>development</id>
            <activation>
                <jdk>1.7</jdk>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <maven.compiler.source>1.7</maven.compiler.source>
                <maven.compiler.target>1.7</maven.compiler.target>
                <maven.compiler.compilerVersion>1.7</maven.compiler.compilerVersion>
            </properties>
        </profile>
    </profiles>
```

