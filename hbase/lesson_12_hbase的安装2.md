前面描述了Hbase的安装, 安装后, 使用`hbase shell`可以使用, 但是使用Java连接Hbase的时候就报错了. 折腾了好久. 终于弄出来了.
报错的内容如下`ERROR: Can't get master address from ZooKeeper; znode data == null`, 关键是报错后, HMaster进程就shutdown了.

这里记录一下处理的过程, 后面空了再研究启动的细节, 弄清楚为什么需要这样配置.

使用的版本是`hadoop-2.6.0 + hbase-1.1.0`. 无论是standalone模式还是pseudo-distributed模式, 在本地客户端一直连接不成功的情况下,
采用了第三中模式`distributed`. 就是使用docker构建一个真正的集群, 3个节点的集群. 3个节点的集群成功启动Hbase, 并使用客户端连接后,
再回头修改standalone模式的配置方式,就能成功连接了. 关于使用docker在单机构建集群的方法, 这个另开一个记录.

step1 下载hbase-1.1.0-bin.tar.gz并解压到/opt目录.
```
wget https://archive.apache.org/dist/hbase/hbase-1.1.0/hbase-1.1.0-bin.tar.gz
tar -zxvf hbase-1.1.0-bin.tar.gz -C /opt
```

step2 修改hbase的配置文件, 一共2个, 分别是`conf/hbase-env.sh`, `conf/hbase-site.xml`. 相关文件的内容如下:
```
$ cat hbase-site.xml
<?xml version="1.0"?>
<?xml-stylesheet type="text/xsl" href="configuration.xsl"?>
<configuration>
   //Here you have to set the path where you want HBase to store its files.
   <property>
      <name>hbase.rootdir</name>
      <value>file:///opt/hbase-1.1.0/hbasedata</value>
   </property>
	
   //Here you have to set the path where you want HBase to store its built in zookeeper  files.
   <property>
      <name>hbase.zookeeper.property.dataDir</name>
      <value>/opt/hbase-1.1.0/zookeeper</value>
   </property>
  <property>
     <name>hbase.zookeeper.quorum</name>
     <value>localhost</value>
  </property>

</configuration>

```
hbase-env.sh文件只需添加JAVA_HOME即可
```
 28 # The java implementation to use.  Java 1.7+ required.
 29 # export JAVA_HOME=/usr/java/jdk1.6.0/
 30 export JAVA_HOME=/opt/jdk1.7.0_80
```

step3 使用Java代码连接Hbase.
pom.xml的依赖如下:
```
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.shgy</groupId>
  <artifactId>hbasedemo</artifactId>
  <version>1.0-SNAPSHOT</version>


  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.7</maven.compiler.source>
    <maven.compiler.target>1.7</maven.compiler.target>
  </properties>

  <dependencies>

    <dependency>
      <groupId>org.apache.hbase</groupId>
      <artifactId>hbase-client</artifactId>
      <version>1.1.0</version>
    </dependency>

    <dependency>
      <groupId>commons-cli</groupId>
      <artifactId>commons-cli</artifactId>
      <version>1.4</version>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>

  </dependencies>

  <build>
      <plugins>
        <plugin>
          <artifactId>maven-assembly-plugin</artifactId>
          <version>3.1.0</version>
          <configuration>
            <archive>
              <manifest>
                <mainClass>com.sgh.NodesInfoDemo</mainClass>
              </manifest>
            </archive>
            <descriptorRefs>
              <descriptorRef>jar-with-dependencies</descriptorRef>
            </descriptorRefs>
          </configuration>
          <executions>
            <execution>
              <id>make-assembly</id> <!-- this is used for inheritance merges -->
              <phase>package</phase> <!-- bind to the packaging phase -->
              <goals>
                <goal>single</goal>
              </goals>
            </execution>
          </executions>
        </plugin>
      </plugins>
  </build>
</project>

```
step4 连接Hbase的Java代码:
```
package  com.shgy;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;


public class HbaseExample {

    public static void main(String... args) throws IOException {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.property.clientPort", "2181");
        config.set("hbase.zookeeper.quorum","localhost");
        //Add any necessary configuration files (hbase-site.xml, core-site.xml)
//        config.addResource(new Path("hbase-site.xml"));

        try (Connection connection = ConnectionFactory.createConnection(config);
             Admin admin = connection.getAdmin()) {
            System.out.println(admin.tableExists(TableName.valueOf("test")));
        }

    }
}
```

这里需要注意的是使用hbase-1.1.0的版本, hbase-1.0.0版本有bug.

参考:

https://www.tutorialspoint.com/hbase/hbase_installation.htm
