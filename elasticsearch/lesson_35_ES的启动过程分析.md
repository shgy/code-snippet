这个是回顾以前的版本, 启动ES，会打印出如下的信息：(以Bootstrap源码启动为例)
```
Connected to the target VM, address: '127.0.0.1:39899', transport: 'socket'
[2018-06-24 22:21:30,165][INFO ][node                     ] [Master Mold] version[2.4.5-SNAPSHOT], pid[5189], build[NA/NA]
[2018-06-24 22:21:30,166][INFO ][node                     ] [Master Mold] initializing ...
[2018-06-24 22:21:31,178][INFO ][plugins                  ] [Master Mold] modules [], plugins [], sites []
[2018-06-24 22:21:31,281][INFO ][env                      ] [Master Mold] using [1] data paths, mounts [[/ (/dev/sda1)]], net usable_space [46.3gb], net total_space [111.7gb], spins? [no], types [ext4]
[2018-06-24 22:21:31,281][INFO ][env                      ] [Master Mold] heap size [2.5gb], compressed ordinary object pointers [true]
[2018-06-24 22:21:34,799][INFO ][node                     ] [Master Mold] initialized
[2018-06-24 22:21:34,799][INFO ][node                     ] [Master Mold] starting ...
[2018-06-24 22:21:34,900][INFO ][transport                ] [Master Mold] publish_address {127.0.0.1:9300}, bound_addresses {127.0.0.1:9300}, {[::1]:9300}
[2018-06-24 22:21:34,908][INFO ][discovery                ] [Master Mold] elasticsearch/F55Ls66FS6uBjarflC9ePg
[2018-06-24 22:21:37,990][INFO ][cluster.service          ] [Master Mold] new_master {Master Mold}{F55Ls66FS6uBjarflC9ePg}{127.0.0.1}{127.0.0.1:9300}, reason: zen-disco-join(elected_as_master, [0] joins received)
[2018-06-24 22:21:38,066][INFO ][http                     ] [Master Mold] publish_address {127.0.0.1:9200}, bound_addresses {127.0.0.1:9200}, {[::1]:9200}
[2018-06-24 22:21:38,067][INFO ][node                     ] [Master Mold] started
[2018-06-24 22:21:38,096][INFO ][gateway                  ] [Master Mold] recovered [0] indices into cluster_state
[2018-06-24 22:23:31,955][INFO ][cluster.metadata         ] [Master Mold] [twitter] creating index, cause [auto(index api)], templates [], shards [5]/[1], mappings []
[2018-06-24 22:23:32,531][INFO ][cluster.routing.allocation] [Master Mold] Cluster health status changed from [RED] to [YELLOW] (reason: [shards started [[twitter][0], [twitter][2], [twitter][2], [twitter][0], [twitter][4]] ...]).
[2018-06-24 22:23:32,640][INFO ][cluster.metadata         ] [Master Mold] [twitter] create_mapping [tweet]
```

启动过程是怎么样的呢？
第一步：读取配置文件，初始化日志。
读取config/ elasticsearch.yml文件，导入相关的启动参数。
读取config/logging.yml文件，导入日志的相关配置信息。
然后就是初始化日志相关的类。ES使用log4j来记录日志信息。
第二步：实例化InternalNode类。
注意：每启动一个ES实例，本质上是启动一个ES的节点(Node)。

2.1 实例化PluginsService。这个过程中会加载用户开发的插件，插件有两种：JvmPlugin和sitePlugin。在启动的时候可以指定参数plugin.mandatory：plugin-a,plugin-b 。如果mandatory plugins没有安装，那么节点就不会启动。 具体实现参考PluginsService的构造函数。
```

```

2.2 装载各种模块。比如NetwordModule,RestModule, TransportModule,RiverModule等等，这个过程中会进行依赖注入的操作。

```
 ModulesBuilder modules = new ModulesBuilder();
            modules.add(new Version.Module(version));
            modules.add(new CircuitBreakerModule(settings));
            // plugin modules must be added here, before others or we can get crazy injection errors...
            for (Module pluginModule : pluginsService.nodeModules()) {
                modules.add(pluginModule);
            }
            modules.add(new PluginsModule(pluginsService));
            modules.add(new SettingsModule(this.settings));
            modules.add(new NodeModule(this));
            modules.add(new NetworkModule(namedWriteableRegistry));
            modules.add(new ScriptModule(this.settings));
            modules.add(new EnvironmentModule(environment));
            modules.add(new NodeEnvironmentModule(nodeEnvironment));
            modules.add(new ClusterNameModule(this.settings));
            modules.add(new ThreadPoolModule(threadPool));
            modules.add(new DiscoveryModule(this.settings));
            modules.add(new ClusterModule(this.settings));
            modules.add(new RestModule(this.settings));
            modules.add(new TransportModule(settings, namedWriteableRegistry));
            if (settings.getAsBoolean(HTTP_ENABLED, true)) {
                modules.add(new HttpServerModule(settings));
            }
            modules.add(new IndicesModule());
            modules.add(new SearchModule());
            modules.add(new ActionModule(false));
            modules.add(new MonitorModule(settings));
            modules.add(new GatewayModule(settings));
            modules.add(new NodeClientModule());
            modules.add(new ShapeModule());
            modules.add(new PercolatorModule());
            modules.add(new ResourceWatcherModule());
            modules.add(new RepositoriesModule());
            modules.add(new TribeModule());
```

2.3 启动各种服务，比如RestController,SearchService,MonitorService等等。
```
 public Node start() {
        if (!lifecycle.moveToStarted()) {
            return this;
        }

        ESLogger logger = Loggers.getLogger(Node.class, settings.get("name"));
        logger.info("starting ...");
        // hack around dependency injection problem (for now...)
        injector.getInstance(Discovery.class).setRoutingService(injector.getInstance(RoutingService.class));
        for (Class<? extends LifecycleComponent> plugin : pluginsService.nodeServices()) {
            injector.getInstance(plugin).start();
        }

        injector.getInstance(MappingUpdatedAction.class).setClient(client);
        injector.getInstance(IndicesService.class).start();
        injector.getInstance(IndexingMemoryController.class).start();
        injector.getInstance(IndicesClusterStateService.class).start();
        injector.getInstance(IndicesTTLService.class).start();
        injector.getInstance(SnapshotsService.class).start();
        injector.getInstance(SnapshotShardsService.class).start();
        injector.getInstance(RoutingService.class).start();
        injector.getInstance(SearchService.class).start();
        injector.getInstance(MonitorService.class).start();
        injector.getInstance(RestController.class).start();

        // TODO hack around circular dependencies problems
        injector.getInstance(GatewayAllocator.class).setReallocation(injector.getInstance(ClusterService.class), injector.getInstance(RoutingService.class));

        injector.getInstance(ResourceWatcherService.class).start();
        injector.getInstance(GatewayService.class).start();

        // Start the transport service now so the publish address will be added to the local disco node in ClusterService
        TransportService transportService = injector.getInstance(TransportService.class);
        transportService.start();
        injector.getInstance(ClusterService.class).start();

        // start after cluster service so the local disco is known
        DiscoveryService discoService = injector.getInstance(DiscoveryService.class).start();


        transportService.acceptIncomingRequests();
        discoService.joinClusterAndWaitForInitialState();

        if (settings.getAsBoolean("http.enabled", true)) {
            injector.getInstance(HttpServer.class).start();
        }
        injector.getInstance(TribeService.class).start();
        if (settings.getAsBoolean("node.portsfile", false)) {
            if (settings.getAsBoolean("http.enabled", true)) {
                HttpServerTransport http = injector.getInstance(HttpServerTransport.class);
                writePortsFile("http", http.boundAddress());
            }
            TransportService transport = injector.getInstance(TransportService.class);
            writePortsFile("transport", transport.boundAddress());
        }
        logger.info("started");

        return this;
    }
```

2.4 经过上面的过程，一个ES节点就启动成功了，通过RESTfull的各种API就可以操作集群了。
