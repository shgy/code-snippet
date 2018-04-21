ES的RESTfull 架构探究

ElasticSearch有一个很重要的特点就是RESTfull API。那么究竟什么是RESTFull? ES又是如何实现RESTfull这一特性呢？

REST是Roy Thomas Fielding在他2000年的博士论文中提出的，是Representational State Transfer的缩写。阮一峰有一篇非常通俗易懂的博客《理解RESTful架构》来讲解RESTfull。里面提到核心的三点：

（1）每一个URI代表一种资源；

（2）客户端和服务器之间，传递这种资源的某种表现层；

（3）客户端通过四个HTTP动词(GET POST PUT DELETE)，对服务器端资源进行操作，实现"表现层状态转化"。

资源不仅仅是存储在服务器上的数据，也可以是一种服务。比如转账、Lucene中的词汇分析(analyze)。



实际上，ES提供了6种HTTP动词，可以在RestController类看到。我们关注几个常用的类。

RestMainAction

学习ES的第一课就是启动ES,然后在浏览器中输入localhost:9200，或者用命令

curl –GET ‘localhost:9200’ 或者curl –HEAD ‘localhost:9200’  得到如下的结果。

```

{

  "status" : 200,

  "name" : "Paste-Pot Pete",

  "cluster_name" : "elasticsearch",

  "version" : {

    "number" : "1.7.0",

    "build_hash" : "${buildNumber}",

    "build_timestamp" : "NA",

    "build_snapshot" : false,

    "lucene_version" : "4.10.4"

  },

  "tagline" : "You Know, for Search"

}

```

为什么必须用GET或者HEAD关键字呢？看RestMainAction的构造方法：

```

@Inject

public RestMainAction(Settings settings, Version version, RestController controller, ClusterName clusterName, Client client, ClusterService clusterService) {

    super(settings, controller, client);

    this.version = version;

    this.clusterName = clusterName;

    this.clusterService = clusterService;

    controller.registerHandler(GET, "/", this);

    controller.registerHandler(HEAD, "/", this);

}

```

“You Know, for Search”这些信息在哪里呢？在RestMainAction.handleRequest()方法里。

```

builder.field("cluster_name", clusterName.value());

builder.startObject("version")

        .field("number", version.number())

        .field("build_hash", Build.CURRENT.hash())

        .field("build_timestamp", Build.CURRENT.timestamp())

        .field("build_snapshot", version.snapshot)

                // We use the lucene version from lucene constants since

                // this includes bugfix release version as well and is already in

                // the right format. We can also be sure that the format is maitained

                // since this is also recorded in lucene segments and has BW compat

        .field("lucene_version", Constants.LUCENE_MAIN_VERSION)

        .endObject();

builder.field("tagline", "You Know, for Search");

builder.endObject();

```

看到激动人心的“You know, for Search”了没？

RestIndexAction

在ES第一课里面，当我们往索引中添加文档，用的命令如下：

curl -XPUT "http://localhost:9200/dept/employee/32" –d "{ \"empname\": \"emp32\"}"

还可以用哪些命令呢？看源码就一目了然了。

```

    @Inject

    public RestIndexAction(Settings settings, RestController controller, Client client) {

        super(settings, controller, client);

        controller.registerHandler(POST, "/{index}/{type}", this); // auto id creation

        controller.registerHandler(PUT, "/{index}/{type}/{id}", this);

        controller.registerHandler(POST, "/{index}/{type}/{id}", this);

        CreateHandler createHandler = new CreateHandler(settings, controller, client);

        controller.registerHandler(PUT, "/{index}/{type}/{id}/_create", createHandler);

        controller.registerHandler(POST, "/{index}/{type}/{id}/_create", createHandler);

    }

```

一切尽在不言中。

RestSearchAction

	搜索功能用到的命令呢？依然在源码中：

```

    @Inject

    public RestSearchAction(Settings settings, RestController controller, Client client) {

        super(settings, controller, client);

        controller.registerHandler(GET, "/_search", this);

        controller.registerHandler(POST, "/_search", this);

        controller.registerHandler(GET, "/{index}/_search", this);

        controller.registerHandler(POST, "/{index}/_search", this);

        controller.registerHandler(GET, "/{index}/{type}/_search", this);

        controller.registerHandler(POST, "/{index}/{type}/_search", this);

        controller.registerHandler(GET, "/_search/template", this);

        controller.registerHandler(POST, "/_search/template", this);

        controller.registerHandler(GET, "/{index}/_search/template", this);

        controller.registerHandler(POST, "/{index}/_search/template", this);

        controller.registerHandler(GET, "/{index}/{type}/_search/template", this);

        controller.registerHandler(POST, "/{index}/{type}/_search/template", this);



        RestExistsAction restExistsAction = new RestExistsAction(settings, controller, client);

        controller.registerHandler(GET, "/_search/exists", restExistsAction);

        controller.registerHandler(POST, "/_search/exists", restExistsAction);

        controller.registerHandler(GET, "/{index}/_search/exists", restExistsAction);

        controller.registerHandler(POST, "/{index}/_search/exists", restExistsAction);

        controller.registerHandler(GET, "/{index}/{type}/_search/exists", restExistsAction);

        controller.registerHandler(POST, "/{index}/{type}/_search/exists", restExistsAction);

    }

```

所以，现在也可以解释我们自己开发的Hello World插件为什么需要写成如下的代码了。

```

public HelloRestHandler(RestController restController) {

       restController.registerHandler(GET, "/_hello", this);

}

```

有没有觉得代码比文档更好用？


