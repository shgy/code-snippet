ES添加数据, 比MySQL要简单, 直接写数据到集群就可以了, 不用事先建表, 建库. 这个就是NoSQL的优势.

添加数据的代码很简单:
```
package com.sgh;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

import org.elasticsearch.node.NodeBuilder;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {


// on startup

        Node node = NodeBuilder.nodeBuilder().settings(Settings.builder()
                .put("path.home", "/home/shgy/tmp/es")
                .put("node.data",false)   // 不能存数据
                .put("node.master",false) // 不能参与选举
        ).node();
        Client client = node.client();

        try{
            IndexResponse indexResp = client.prepareIndex("twitter","tweet","1")
                    .setSource("id",1,"name","hello")
                    .get();
            System.out.println(indexResp.getIndex());
            System.out.println(indexResp.getType());
            System.out.println(indexResp.getId());
            System.out.println(indexResp.isCreated());

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // on shutdown
            node.close();
        }

    }
}

```

关于参数`operationThreaded`参数, 这个的影响, 后面再关注. 这里记录一下.

