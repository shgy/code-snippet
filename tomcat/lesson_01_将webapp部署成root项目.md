两步即可：
```
mv ROOT root_def
mv webapp.war ROOT.war
```

如果不想改，还有第二种办法， 那就是通过在conf/Catalina/localhost目录下添加配置文件：
```
$ mv /opt/apache-tomcat-7.0.85/webapps/ROOT /opt/apache-tomcat-7.0.85/webapps/root-default

$ cat /opt/apache-tomcat-7.0.85/conf/Catalina/localhost/ROOT.xml 
<Context path="" docBase="/home/shgy/tmp/mywebapp.war" debug="0" privileged="true">
</Context>
```
这样`/home/shgy/tmp/mywebapp.war`就会自动解压到webapps目录下的ROOT目录了。

