编译hadoop
```
mvn package -Pdist,native -DskipTests -Dtar
```

验证本地库是否加载成功：hadoop checknative