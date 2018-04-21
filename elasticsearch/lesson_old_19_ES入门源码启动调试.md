ES的源码启动和调试分析

第一步，从github上下载ElasticSearch的源码，并解压

第二步，将项目作为maven项目导入到eclipse中

第三步，启动ElasticSearch,启动类为：org.elasticsearch.bootstrap.Bootstrap.java。启动参数

vm argument如下：

```

-server -Des.foreground=true  -Des.path.home=D:\source_code\elasticsearch-1.3.4

-Dpath.plugins=D:\source_code\elasticsearch-1.3.4\plugins

```

由于ES用Netty进行通信，所以很方便地可以定位到org.elasticsearch.http.netty. HttpRequestHandler.messageReceived()方法。所有的Http请求都会启用messageReceived()方法。

所以在该方法里面设断点，就可以调试索引文档和搜索文档的处理过程了。

为什么不分析启动过程呢？由于启动过程需要对ElasticSearch的整体框架有全面的理解，并不适合在这里入手分析。


