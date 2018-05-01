assembly插件用户给jar包打包, 相比`maven-jar-plugin`, 它可以自动将所有依赖集成到一个jar包中, 方便使用.
配置如下:
```
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
```
注意没有`pluginmanagement`这个中间商. `pluginmanagement`这个元素用于maven项目有module且需要集成的情况.

这里executions的功能是将assemble这个插件绑定到package这个phase, 需要执行的goal是single, 这样执行`mvn package`是就会自动调用assemble插件的功能.


命令如下:
```
mvn package
```
或者
```
mvn compile assembly:single
```
最后会生成jar-with-dependencies

参考:
https://stackoverflow.com/questions/10483180/maven-what-is-pluginmanagement?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
