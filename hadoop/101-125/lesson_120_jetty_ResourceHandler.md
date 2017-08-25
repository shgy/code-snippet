在前面的学习笔记中讲到ContextHandlerCollection, 没有明白其功能。今天正好看到了Resource Handler，
在最后的demo中， 用到了ContextHandlerCollection, 
感觉也许可以通过ResourceHandler理解到ContextHandlerCollection的功能。

用了一下ResourceHandler， 感觉真是个好东西。 通过他， 可以只用7行代码搭建一个简单的静态web服务器。 这在日常工作中
就很方便了。 比如我用的是ubuntu(没有好用的QQ) , 需要传输个文件啥的， 直接启动一个服务器。 然后分享host:port 即可实现文件的传输。
在windows中, 要访问ubuntu的文件系统，得安装设置samba, 每次登录还得输入用户名密码。

```
public class ResourceHandlerDemo {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8090);

        ResourceHandler rh = new ResourceHandler();
        rh.setDirectoriesListed(true);
        rh.setBaseResource(Resource.newResource(System.getProperty("user.dir")));
        server.setHandler(rh);
        server.start();
        server.join();
    }
}
```

