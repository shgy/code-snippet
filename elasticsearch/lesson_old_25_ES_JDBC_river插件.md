ES插件开发之JDBC River插件 
把数据导入到索引中是很常见的需求，比如数据库中的数据、爬虫爬来的网页数据……。在ES，用River这种可插拔的Serivce来实现上述需求。 
下面以一个简单的jdbc-river插件来记录River插件的开发过程。 
第一步，建立maven项目，命名为riverjdbc，在pom.xml中引入相应的dependency和plugin。最终的pom.xml如下：

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>org.es.plugin.riverjdbc</groupId>
  <artifactId>riverjdbc</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>jar</packaging>
  <name>riverjdbc</name>
  <url>http://maven.apache.org</url>
  <build>
        <plugins>
            <!-- Generate the release zip file (run during package step) -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>2.2.1</version>
                <configuration>
                    <finalName>elasticsearch-${project.name}-${elasticsearch.version}</finalName>
                    <appendAssemblyId>false</appendAssemblyId>
                    <outputDirectory>${project.build.directory}/release/</outputDirectory>
                    <descriptors>
                        <descriptor>assembly/release.xml</descriptor>
                    </descriptors>
                </configuration>
                <executions>
                    <execution>
                        <id>generate-release-plugin</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
        <resources>
            <resource>
                <directory>src/main/resources</directory>
                <filtering>true</filtering>
                <includes>
                    <include>**/*.properties</include>
                </includes>
            </resource>
        </resources>
    </build>
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <elasticsearch.version>1.3.4</elasticsearch.version>
  </properties>
  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.8.2</version>
      <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>5.1.32</version>
    </dependency>
    <dependency>
        <groupId>org.elasticsearch</groupId>
        <artifactId>elasticsearch</artifactId>
        <version>${elasticsearch.version}</version>
    </dependency>
  </dependencies>
</project>
第二步，创建maven-assembly-plugin的配置文件assembly/release.xml

<?xml version="1.0"?>
<assembly>
    <id>bin</id>
    <formats>
        <format>zip</format>
    </formats>
    <includeBaseDirectory>false</includeBaseDirectory>
    <dependencySets>
        <dependencySet>
            <unpack>false</unpack>
            <outputDirectory>/</outputDirectory>
            <useProjectArtifact>false</useProjectArtifact>
            <useTransitiveFiltering>true</useTransitiveFiltering>
            <excludes>
                <!-- --> <exclude>org.elasticsearch:elasticsearch</exclude>
                <exclude>junit:junit</exclude>
            </excludes>
        </dependencySet>
    </dependencySets>
    <fileSets>
        <fileSet>
            <directory>${project.build.directory}/</directory>
            <outputDirectory>/</outputDirectory>
            <includes>
                <include>${project.name}-${project.version}.jar</include>
            </includes>
        </fileSet>
    </fileSets>
</assembly>
第三步，创建plugin的入口，JDBCRiverPlugin.java。

package org.es.plugin.riverjdbc;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.river.RiversModule;
public class JDBCRiverPlugin extends AbstractPlugin{
    public void onModule(RiversModule module) {
        module.registerRiver("jdbcriver", JDBCRiverModule.class);
    }
    public String name() {
        // TODO Auto-generated method stub
        return "JDBCRiver";
    }
    public String description() {
        // TODO Auto-generated method stub
        return "JDBCRiver Plugin";
    }
}
并在es-plugin.properties文件中配置入口类。 
plugin=org.es.plugin.riverjdbc.JDBCRiverPlugin 
version=project.versionhash={buildNumber} 
timestamp=timestampdate={tstamp} 
第四步，创建Guice的注入配置类JDBCRiverModule.java

package org.es.plugin.riverjdbc;
import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.river.River;
public class JDBCRiverModule extends AbstractModule{
    @Override
    protected void configure() {
        // TODO Auto-generated method stub
        bind(River.class).to(JDBCRiver.class).asEagerSingleton();
    }
}
第五步，创建River的实现类JDBCRiver.java。

package org.es.plugin.riverjdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.river.AbstractRiverComponent;
import org.elasticsearch.river.River;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;
public class JDBCRiver extends AbstractRiverComponent implements River {
    private RiverName riverName;
    private RiverSettings settings;
    private Client client;
    boolean start = false;
    @Inject
    protected JDBCRiver(RiverName riverName, RiverSettings settings,Client client) {
        super(riverName, settings);
        this.riverName = riverName;
        this.settings = settings ;
        this.client = client;
    }
    public void start() {
        // TODO Auto-generated method stub
        logger.info("start jdbc river……"+settings.settings().toString());
        Map<String,Object> params = settings.settings();
        String url =  (String) params.get("url");
        String user =  (String) params.get("user");
        String pass =  (String) params.get("pass");
        String sql = (String) params.get("sql");
        Connection conn= null;
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try{
            conn = DriverManager.getConnection(url, user, pass);
            pstm = conn.prepareStatement(sql);
            rs = pstm.executeQuery();
            ResultSetMetaData  rsMeta = rs.getMetaData();
            int columnCount = rsMeta.getColumnCount();
            BulkRequest bulk = new BulkRequest();
            while(rs.next()){
                IndexRequest req = new IndexRequest("database","table");
                for(int i=1;i<=columnCount;i++){
                    req.source(rsMeta.getColumnLabel(i), rs.getObject(i));
                }
                bulk.add(req);
            }
            client.bulk(bulk).actionGet();
        }catch (Exception e) {
            logger.error("exception", e);
        }finally{
            try {
                if(rs!=null)rs.close();
                if(pstm!=null)pstm.close();
                if(conn!=null)conn.close();
            } catch (Exception e2) {}
        }
    }
    public void close() {
        // TODO Auto-generated method stub
        logger.info("stop jdbc river……");
        start = false;
    }
}
第六步，打包程序并安装到ES中。 
在eclipse中打包[run as]-[maven build]，或者到项目根目录下输入命令mvn package。 
安装的命令如下： bin/plugin –u file:///river-plugin-path –i jdbc_river 
安装成功后，会在ES的plugins目录下生成如下的目录结构：

│
├── jdbc_river
│   └── mysql-connector-java-5.1.32.jar
│   └── riverjdbc-0.0.1-SNAPSHOT.jar
├── head
第七步，启动ES，并初始化jdbc_river。

curl -XPUT 'localhost:3306:9200/_river/jdbcriver/_meta' -d '{"type" : "jdbcriver","url" : "jdbc:mysql:// 'localhost:3306/xh","user" : "root","pass" : "admin","sql" : "select * from user"}'
数据库表的内容如下：

初始化完成后，通过head插件查看ES中的数据如下：

上述过程只是简单地给了JDBC River的一个简单框架，离成熟的插件相差甚远。本例也没有考虑到增量、批处理等细节问题。如果深入思考一下前面的hello world插件，容易发现通过RestHandler也是可以实现从数据库中导入数据，River的实现方式有什么不同呢？ 
在集群中，River是以单例的方式存在，只会在一个节点(node)上运行。如果该节点出问题了，那么River会转移到其它节点(node)上运行。专业的说法：River具有容错性。
