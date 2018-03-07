assembly插件用户给jar包打包, 其配置如下:
```
<plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>Provider</mainClass>
                        </manifest>
                    </archive>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
            </plugin>
```
命令如下:
```
mvn package
```
或者
```
mvn compile assembly:single
```
最后会生成jar-with-dependencies
