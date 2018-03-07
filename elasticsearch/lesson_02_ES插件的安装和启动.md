es-2.2.1版本安装插件。

以kopf插件为例：

1. 到插件的github下载插件， 然后使用如下的命令安装。 
```
./bin/plugin install file:/home/shgy/Downloads/elasticsearch-kopf-2.1.2.zip 
```
2. 启动elasticsearch
```
./bin/elasticsearch
```
3. 在浏览器中访问es
```
http://localhost:9200/_plugin/kopf/
```

问题1： 在ubuntu16.04中, 直接从github下载会出现http.protocol_version问题
问题2： 插件的根目录下需要有`plugin-descriptor.properties`文件