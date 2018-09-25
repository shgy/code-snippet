ES中，有一类参数是可以动态调整的，比如副本数量： `number_of_replicas`。
在插件开发中，如何添加自己的自定义参数呢？
在插件的入口，添加`onModule(ClusterModule module)`即可。

```
public class ShgyPlugin extends Plugin {
    @Override
    public String name() {
        return "shgy-plugin";
    }

    @Override
    public String description() {
        return "shgy plugin for bitmap";
    }

    public void onModule(ClusterModule module){

        module.registerIndexDynamicSetting("index.custom_setting", new Validator() {
            @Override
            public String validate(String setting, String value, ClusterState clusterState) {
                if (value == null) {
                    throw new NullPointerException("value must not be null");
                }
                return null;
            }
        });
    }
}
```

编译代码，安装插件后，使用如下的脚本测试:
```
curl -X PUT "localhost:9200/twitter/_settings" -H 'Content-Type: application/json' -d'
{
    "index" : {
        "custom_setting" : 2
    }
}

curl -XGET 'http://localhost:9200/twitter/_settings?pretty'
'
```


在代码中使用参数,一般是在TransportAction中使用， 代码片段如下：
```
  ClusterState clusterState = clusterService.state();
  clusterState.blocks().globalBlockedRaiseException(ClusterBlockLevel.READ);

  String concreteSingleIndex = indexNameExpressionResolver.concreteSingleIndex(clusterState, request);

  IndexMetaData indexMeta = clusterState.getMetaData().index(concreteSingleIndex);
  int sectionCnt = indexMeta.getSettings().getAsInt("index.custom_settings",-1);
```

即通过clusterService获取到clusterState, 然后获取到IndexMetaData, 然后获取到Settings。


自定义动态参数， 配合templates的使用，就不需要频繁手动创建索引了。 这个知识点应该归纳到 ES插件开发的一部分。
