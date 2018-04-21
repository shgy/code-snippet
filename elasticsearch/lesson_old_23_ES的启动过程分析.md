启动ES，会打印出如下的信息：(以Bootstrap源码启动为例)

启动过程是怎么样的呢？

第一步：读取配置文件，初始化日志。

读取config/elasticsearch.yml文件，导入相关的启动参数。

读取config/logging.yml文件，导入日志的相关配置信息。

然后就是初始化日志相关的类。ES使用log4j来记录日志信息。

第二步：实例化InternalNode类。

注意：每启动一个ES实例，本质上是启动一个ES的节点(InternelNode)。

2.1 实例化PluginsService。这个过程中会加载用户开发的插件，插件有两种：JvmPlugin和sitePlugin。在启动的时候可以指定参数plugin.mandatory：plugin-a,plugin-b 。

如果mandatory plugins没有安装，那么节点就不会启动。

具体实现参考PluginsService的构造函数。

2.2 装载各种模块。比如NetwordModule,RestModule, TransportModule,RiverModule等等，这个过程中会进行依赖注入的操作。

```
# org.elasticsearch.node.internal.InternalNode.java
           ModulesBuilder modules = new ModulesBuilder();

            modules.add(new Version.Module(version));

            modules.add(new CacheRecyclerModule(settings));

            modules.add(new PageCacheRecyclerModule(settings));

            modules.add(new CircuitBreakerModule(settings));

            modules.add(new BigArraysModule(settings));

            modules.add(new PluginsModule(settings, pluginsService));

            modules.add(new SettingsModule(settings));

            modules.add(new NodeModule(this));

            modules.add(new NetworkModule());

            modules.add(new ScriptModule(settings));

            modules.add(new EnvironmentModule(environment));

            modules.add(new NodeEnvironmentModule(nodeEnvironment));

            modules.add(new ClusterNameModule(settings));

            modules.add(new ThreadPoolModule(threadPool));

            modules.add(new DiscoveryModule(settings));

            modules.add(new ClusterModule(settings));

            modules.add(new RestModule(settings));

            modules.add(new TransportModule(settings));

            if (settings.getAsBoolean(HTTP_ENABLED, true)) {

                modules.add(new HttpServerModule(settings));

            }

            modules.add(new RiversModule(settings));

            modules.add(new IndicesModule(settings));

            modules.add(new SearchModule());

            modules.add(new ActionModule(false));

            modules.add(new MonitorModule(settings));

            modules.add(new GatewayModule(settings));

            modules.add(new NodeClientModule());

            modules.add(new BulkUdpModule());

            modules.add(new ShapeModule());

            modules.add(new PercolatorModule());

            modules.add(new ResourceWatcherModule());

            modules.add(new RepositoriesModule());

            modules.add(new TribeModule());



            injector = modules.createInjector();



            client = injector.getInstance(Client.class);

            threadPool.setNodeSettingsService(injector.getInstance(NodeSettingsService.class));

```
2.3 启动各种服务，比如RestController,SearchService,MonitorService等等。

```

# org.elasticsearch.node.internal.InternalNode.java   start()方法


        injector.getInstance(MappingUpdatedAction.class).start();

        injector.getInstance(IndicesService.class).start();

        injector.getInstance(IndexingMemoryController.class).start();

        injector.getInstance(IndicesClusterStateService.class).start();

        injector.getInstance(IndicesTTLService.class).start();

        injector.getInstance(RiversManager.class).start();

        injector.getInstance(SnapshotsService.class).start();

        injector.getInstance(TransportService.class).start();

        injector.getInstance(ClusterService.class).start();

        injector.getInstance(RoutingService.class).start();

        injector.getInstance(SearchService.class).start();

        injector.getInstance(MonitorService.class).start();

        injector.getInstance(RestController.class).start();




```

经过上面的过程，一个ES节点就启动成功了，通过RESTfull的各种API就可以操作集群了。

<由于不理解ES的架构，这里的细节有待继续探索>


