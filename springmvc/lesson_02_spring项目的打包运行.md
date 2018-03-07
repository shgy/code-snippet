需求: 将spring项目打包成可执行jar包, 使用的是maven插件是: assemble. 打包没有问题, 运行出问题:
```
Exception in thread "main" org.springframework.beans.factory.parsing.BeanDefinitionParsingException: 
Configuration problem: Unable to locate Spring NamespaceHandler for XML schema namespace [http://www.springframework.org/schema/context]
```
网上查询, 得到解决方案, 换用shade插件, 配置如下:
```
<plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version> 1.7.1</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <transformers>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                                    <resource>META-INF/spring.handlers</resource>
                                </transformer>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.AppendingTransformer">
                                    <resource>META-INF/spring.schemas</resource>
                                </transformer>
                                <transformer
                                        implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>Provider</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

```
参考:
http://blog.csdn.net/qing0706/article/details/51612040
