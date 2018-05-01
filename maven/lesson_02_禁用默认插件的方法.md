###查看phase默认插件的execution的id 见maven pom进阶教程 - 插件与继承(plugin & inherite)

###禁用默认插件的方法 方法1: 改掉默认插件配置的phase(官方文档上并没有写这个方法)

<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-jar-plugin</artifactId>
	<version>3.0.2</version>
	<executions>
		<execution>
			<id>default-jar</id> <!-- default-jar是maven默认打包jar的execution的id -->
			<phase>none</phase> <!-- 随便写，只要不存在就行 -->
		</execution>
	</executions>
</plugin>
方法2: 设置skip为true, 只有部分插件支持这个参数, 使用之种方法之前，最好先去查阅一下plugin的配置资料

<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-resources-plugin</artifactId>
	<version>3.0.2</version>
	<executions>
		<execution>
			<id>default-resources</id><!-- process-resources阶段的默认有execution的id -->
			<configuration>
				<skip>true</skip>　<!--跳过本次任务-->
			</configuration>
		</execution>
	</executions>
</plugin>
###修改默认插件的参数 与上一节类似，指明execution的id为默认id 比如，将拷贝资源的操作更改到validate阶段执行

<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-resources-plugin</artifactId>
	<version>3.0.2</version>
	<executions>
		<execution>
			<id>default-resources</id><!-- process-resources阶段的默认有execution的id -->
			<phase>validate</phase>
		</execution>
	</executions>
</plugin>

参考:
https://my.oschina.net/u/2343729/blog/830929
