各个业务数据汇总到hive, 经过ETL处理后， 导出到产品数据库是大数据产品的典型业务流程。这其中，sqoop(离线)和kafka(实时)几乎是数据总线的标配了。
但是有些业务也有不标准的，比如hive数据导入到es. hive数据导入到ES, 官方组件是elasticsearch-hadoop. 其用法在前面的博客中已有介绍。 那么其实现原理是怎样的呢？ 或者说， es-hadoop这家伙到底是怎么把hive表的数据弄到es中去的？ 为了弄清楚这个问题， 我们首先需要有一个本地的源码环境。

s1: 下载elasticsearch-hadoop源码。
```
git clone https://github.com/elastic/elasticsearch-hadoop.git
```

s2: 编译源码。直接编译master即可。
```
gradlew distZip
```


s3: 编译成功后，导入到intellij。 这里注意导入build.gradle文件，就像maven项目导入pom文件一样。


s4: 在intellij中编译一次项目。


s5: 在本地启动一个es, 默认的端口即可。

s6: 运行测试用例`AbstractHiveSaveTest.testBasicSave()`。 直接运行是会报错的， 需要略微修改一下代码,添加一个类的属性:
```
    @ClassRule
    public static ExternalResource hive = HiveSuite.hive;
```
如何是在windows环境下，需要新建包`org.apache.hadoop.io.nativeio`, 然后在该包下建立`NativeIO.java`类。 修改代码如下:
```
// old
    public static boolean access(String path, AccessRight desiredAccess)
        throws IOException {
      return access0(path, desiredAccess.accessRight());
    }

// new 
    public static boolean access(String path, AccessRight desiredAccess)
        throws IOException {
      return true;
    }

```

这样就运行起来了一个本地的hive到es的代码。 由于我的目标是弄清楚一条主线: `es-hadoop这家伙到底是怎么把hive表的数据弄到es中去的？`
在elasticsearch-hadoop这个比较庞大的项目中，修改代码也比较麻烦，因此单独建立一个项目hive-es, 然后把这个测试类进行改造， 跑通该方法即可。
由于对gradle不熟悉， 还是建立maven项目， 项目的依赖如下:
```
    <repositories>
        <repository>
            <id>spring-libs</id>
            <url>http://repo.spring.io/libs-milestone/</url>
        </repository>
    </repositories>
    <dependencies>

        <dependency>
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-1.2-api</artifactId>
            <version>2.6.2</version>
            <scope>test</scope>
        </dependency>

        <dependency>     <!-- 桥接：告诉Slf4j使用Log4j2 -->
            <groupId>org.apache.logging.log4j</groupId>
            <artifactId>log4j-slf4j-impl</artifactId>
            <version>2.6.2</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.lmax</groupId>
            <artifactId>disruptor</artifactId>
            <version>3.3.6</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.11</version>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.apache.hive</groupId>
            <artifactId>hive-cli</artifactId>
            <version>1.2.1</version>
            <scope>provided</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.logging.log4j</groupId>
                    <artifactId>log4j-slf4j-impl</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>

        <dependency>
            <groupId>org.apache.hadoop</groupId>
            <artifactId>hadoop-client</artifactId>
            <version>2.2.0</version>
            <scope>provided</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.apache.logging.log4j</groupId>
                    <artifactId>log4j-slf4j-impl</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.slf4j</groupId>
                    <artifactId>slf4j-log4j12</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.elasticsearch</groupId>
            <artifactId>elasticsearch-hadoop</artifactId>
            <version>6.3.0</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```
这里用到了log4j2, 所以日志类放在前面。接下来迁移测试代码。迁移的原则是 `若无必要，不新增类`。 如果只用到了类的一个方法，那么只迁移一个方法。

个人感觉这里比较巧妙的是，启动了一个嵌入式的hive实例。能够执行hive sql, 而且是在一个jvm中， 这个太酷了。对于学习研究来说，太酷了。






基础的环境搭建好后，就可以研究es-hive的源码了， 先看源码的结构:
```
elasticsearch-hadoop/hive/src/main/java/org/elasticsearch/hadoop/hive$ tree .
.
├── EsHiveInputFormat.java
├── EsHiveOutputFormat.java
├── EsSerDe.java
├── EsStorageHandler.java
├── HiveBytesArrayWritable.java
├── HiveBytesConverter.java
├── HiveConstants.java
├── HiveFieldExtractor.java
├── HiveType.java
├── HiveUtils.java
├── HiveValueReader.java
├── HiveValueWriter.java
├── HiveWritableValueWriter.java
└── package-info.java

0 directories, 14 files

```






