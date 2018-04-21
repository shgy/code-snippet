ElasticSearch中的很多功能都可用插件来完成，换句话说，插件给了ElasticSearch无限可能。与其它的应用学习一样，本文也给出一个hello world案例。

第一步，创建一个maven项目es-helloplugin，pom.xml配置如下：

```

<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

  <modelVersion>4.0.0</modelVersion>

  <groupId>org.elasticsearch.helloplugin</groupId>

  <artifactId>helloplugin</artifactId>

  <version>0.0.1-SNAPSHOT</version>

  <name>helloplugin</name>

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

		<elasticsearch.version>1.3.4</elasticsearch.version>

	</properties>

	<dependencies>

		<dependency>

			<groupId>junit</groupId>

			<artifactId>junit</artifactId>

			<version>4.8.2</version>

		</dependency>

		<dependency>

			<groupId>org.elasticsearch</groupId>

			<artifactId>elasticsearch</artifactId>

			<version>${elasticsearch.version}</version>

		</dependency>

	</dependencies>

</project>

```

第二步，在项目根路径下创建assembly文件夹，在assembly文件夹里创建release.xml。内容如下：

```

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

				<exclude>org.elasticsearch:elasticsearch</exclude>

				<exclude>junit:junit</exclude>

			</excludes>

		</dependencySet>

	</dependencySets>

	<fileSets>

		<fileSet>

			<directory>${project.build.directory}/</directory>

			<outputDirectory>/</outputDirectory>

			<includes>

				<include>elasticsearch-${project.name}-${elasticsearch.version}.jar</include>

			</includes>

		</fileSet>

	</fileSets>

</assembly>

```

第三步，编写代码，创建如下的三个类。

```

ExamplePlugin.java

package org.elasticsearch.helloplugin;



import java.util.ArrayList;

import java.util.Collection;



import org.elasticsearch.common.inject.Module;

import org.elasticsearch.plugins.AbstractPlugin;



public class ExamplePlugin extends AbstractPlugin{



	public String name() {

		  return "example-plugin";

	}



	public String description() {

		return "Example Plugin Description";

	}



	@Override

	public Collection<Class<? extends Module>> modules() {

		Collection<Class<? extends Module>> modules = new ArrayList<Class<? extends Module>>();

        modules.add(ExampleRestModule.class);

        return modules;

	}



}

```

```

ExampleRestModule.java

package org.elasticsearch.helloplugin;



import org.elasticsearch.common.inject.AbstractModule;



public class ExampleRestModule extends AbstractModule{



	@Override

	protected void configure() {

		bind(HelloRestHandler.class).asEagerSingleton();

	}



}

```

```

HelloRestHandler.java

package org.elasticsearch.helloplugin;



import org.elasticsearch.common.inject.Inject;

import org.elasticsearch.rest.BytesRestResponse;

import org.elasticsearch.rest.RestChannel;

import org.elasticsearch.rest.RestController;

import org.elasticsearch.rest.RestHandler;

import org.elasticsearch.rest.RestRequest;



import static org.elasticsearch.rest.RestRequest.Method.GET;

import static org.elasticsearch.rest.RestStatus.OK;

public class HelloRestHandler implements RestHandler  {

	 @Inject

    public HelloRestHandler(RestController restController) {

        restController.registerHandler(GET, "/_hello", this);

    }

	public void handleRequest(RestRequest request, RestChannel channel)

			throws Exception {

		// TODO Auto-generated method stub

		String who = request.param("who");

        String whoSafe = (who!=null) ? who : "world";

        channel.sendResponse(new BytesRestResponse(OK, "Hello, " + whoSafe + "!"));

	} 

}

```

第四步，创建es-plugin.properties文件，内容如下：

plugin=org.elasticsearch.helloplugin.ExamplePlugin

最后，整个项目的目录结构如下：

```

│

├── pom.xml

├── assembly

│   └── plugin.xml

├── src

│   └── main

│       ├── java

│       │   └── org

│       │       └── elasticsearch

│       │           └── helloplugin

│       │               └── ExamplePlugin.java

│       │               ├── ExampleRestModule.java

│       │               ├── HelloRestHandler.java

│       └── resources

│           └── es-plugin.properties

│

```

第五步，项目打包。在eclipse中运行[Run As][Maven install]， 



成功后会在target/release目录下生成elasticsearch-helloplugin-1.3.4.zip。

第六步，安装插件。命令如下：

```

plugin –u file:///d:\es_plugin\elasticsearch-helloplugin-1.3.4.zip -i example-plugin

```

第七步，启动elasticsearch，在浏览器中输出localhost:9200/_hello和localhost:9200/_hello?who=ElasticSearch，得到的结果分别如下：





参考文档：

https://www.found.no/foundation/writing-a-plugin/


