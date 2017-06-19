通常我们使用hive不会直接在命令行中使用，而是通过hiveserver2.

在使用python连接hive执行sql, 
执行 `select * from default.test`时， 一切正常。
执行`select count(*) from default.test`时，发生了如下的异常：
```
thrift.transport.TTransport.TTransportException: TSocket read 0 bytes
```

怎么办呢？ 打开hive的debug日志， 然后启动hiveserver2
```
hiveserver2 --hiveconf hive.root.logger=DEBUG,console 
```
然后再执行报错的sql, 服务端的日志如下：
```
 Missing version in readMessageBegin, old client?
```
多看一些报错信息：
```
Caused by: java.lang.ClassNotFoundException: org.apache.tez.dag.api.client.StatusGetOpts
```

即hive使用了tez引擎执行了sql， 配置hive.execution.engine=mr后， 使用`show conf "hive.execution.engine"`， 显示
```
[['mr', 'STRING', 'Expects one of [mr, tez, spark]. 
Chooses execution engine. Options are: mr (Map reduce, default), tez (hadoop 2 only), spark']]
```

再看python连接hive的connection
```
    def connect_hive_thrift(self):

        return pyhs2.connect(host=self.ds_host,  # node11.56qq.com
                             port=self.ds_port,  #
                             authMechanism="PLAIN",
                             user=self.ds_user,
                             password=self.ds_passwd,
                             database=self.ds_db,
                             configuration={"hive.execution.engine": "tez"})
```

修改成mr后问题解决。
