编译hadoop
```
mvn package -Pdist,native -DskipTests -Dtar
```
将编译好的lib目录copy到/opt/hadoopt-2.6.0/目录下编译的目录为hadoop-dist/target/hadoop-2.6.0
验证本地库是否加载成功：hadoop checknative