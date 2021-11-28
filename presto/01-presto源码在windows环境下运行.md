presto源码不支持在windows环境下执行。 需进行一系列的处理才能正常在windows下执行。
在插件开发，理解核心架构等需求下，着手准备在windows环境的intellij idea中执行源码。
以presto-0.240为例

Step1: 安装jdk， cgywin。 确保在intellij的terminal中可以正常执行chmod命令(主要是设置cgywin的path)

Step2: git clone源码，checkout到指定分支，然后准备编译源码。在编译前，需要进行一些设置。
注释root下的pom.xml文件中： 
```
<air.checkstyle.config-file>src/checkstyle/presto-checks.xml</air.checkstyle.config-file>
<module>presto-docs</module>
```
添加
```
        <air.check.skip-extended>true</air.check.skip-extended>
        <air.javadoc.lint>-missing</air.javadoc.lint>
```

Step3: 修改presto-maven-plugin源码
```
ServiceDescriptorGenerator.java
修改前：
String className = classPath.substring(0, classPath.length() - 6).replace('/', '.');
修改后:
String className = classPath.substring(0, classPath.length() - 6).replace(File.separatorChar, '.');
```
然后mvn clean install即可

Step4: mvn clean install -DskipTests编译presto源码

Step5: mvn idea:idea 将源码处理可以被intellij idea正常载入

Step6: 运行Presto源码
vm arguments
```
 -ea -XX:+UseG1GC -XX:G1HeapRegionSize=32M -XX:+UseGCOverheadLimit -XX:+ExplicitGCInvokesConcurrent -Xmx2G -Dconfig=etc/config.properties -Dlog.levels-file=etc/log.properties
```
working directory
```
$MODULE_DIR$
```

presto-main
```
etc/config.properties文件
plugin.dir=D:\\presto-server-0.240\\plugin
#plugin.bundles=\
#  ../presto-blackhole/pom.xml,\
#  ../presto-memory/pom.xml,\
#  ../presto-jmx/pom.xml,\
#  ../presto-raptor/pom.xml,\
#  ../presto-hive-hadoop2/pom.xml,\
#  ../presto-example-http/pom.xml,\
#  ../presto-kafka/pom.xml, \
#  ../presto-tpch/pom.xml, \
#  ../presto-local-file/pom.xml, \
#  ../presto-mysql/pom.xml,\
#  ../presto-sqlserver/pom.xml, \
#  ../presto-postgresql/pom.xml, \
#  ../presto-tpcds/pom.xml, \
#  ../presto-i18n-functions/pom.xml,\
#  ../presto-function-namespace-managers/pom.xml,\
#  ../presto-druid/pom.xml

etc/catalog/hive.properties文件
hive.dfs.require-hadoop-native=false

PrestoSystemRequirements.java文件

  public static void verifyJvmRequirements()
    {
        verifyJavaVersion();
        verify64BitJvm();
//        verifyOsArchitecture();
        verifyByteOrder();
        verifyUsingG1Gc();
//        verifyFileDescriptor();
        verifySlice();
    }
```

处理完成上述步骤，presto就可以正常启动了，启动类： PrestoServer

然后用presto-cli连接到presto就可以执行sql进行debug了。
```
/mnt/d/presto-server-0.240/cli/presto --server localhost:8080 --catalog tpch
```
研究源码，其实不建议直接debug， 先理解核心主流程，debug只是为了验证流程理解以及部分处理细节。
