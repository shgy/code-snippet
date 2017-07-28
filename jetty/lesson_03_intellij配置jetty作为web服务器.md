1. 下载jetty 并解压。
```
wget http://central.maven.org/maven2/org/eclipse/jetty/jetty-distribution/9.2.12.M0/jetty-distribution-9.2.12.M0.tar.gz

$ tree /opt/jetty -L 2
.
├── jetty-distribution-9.2.12.M0
│   ├── bin
│   ├── demo-base
│   ├── etc
│   ├── lib
│   ├── license-eplv10-aslv20.html
│   ├── logs
│   ├── modules
│   ├── notice.html
│   ├── README.TXT
│   ├── resources
│   ├── start.ini
│   ├── start.jar
│   ├── VERSION.txt
│   └── webapps
└── jetty-distribution-9.2.12.M0.tar.gz
```
2. 在intellij中下载jetty插件。 [File] --> [settings] --> [plugins]

3. 在intellij中配置jetty服务器。 [run] --> [edit configurations] --> [+]

参考:
http://blog.csdn.net/xiejx618/article/details/49936541
