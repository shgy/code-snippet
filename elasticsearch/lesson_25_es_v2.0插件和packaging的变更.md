1. 配置的path可以是符号链接，但是path内部不能是符号链接。

2. command line 参数必须在内置 static parameters后面，比如`-d -p`
```
bin/elasticsearch -d -p /tmp/foo.pid --http.cors.enabled=true --http.cors.allow-origin='*'
```

3. Plugin manager必须使用root权限运行。
   RPM和deb包

4. plugin需要descriptor file 

