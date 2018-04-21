GetAPI 就是通过Id获取索引的文档信息. 由于ES的返回的文档内容比较多, 给个简单的样例:
```
{
"_index": "twitter",
"_type": "tweet",
"_id": "1",
"_score": 1,
"_source": {
"id": 1,
"name": "hello"
}
}
```
有时候, 我们只是需要部分内容. 比如文章数据, 标题很段, 内容很长. 那么有些场景只需要标题时, 这样的设置能提高系统吞吐量和接口的响应速度.

这波操作也是相当简单:
```
package com.sgh;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

public class GetData {
    public static void main(String[] args) {
        // on startup

        Node node = NodeBuilder.nodeBuilder().settings(Settings.builder()
                .put("path.home", "/home/shgy/tmp/es")
                .put("node.data",false)   // 不能存数据
                .put("node.master",false) // 不能参与选举
        ).node();
        Client client = node.client();

        try{
            GetResponse gexResp = client.prepareGet("twitter","tweet","1")
                    .setFields("name")
                    .setFetchSource(true)
                    .get();
            System.out.println(gexResp.getIndex());
            System.out.println(gexResp.getType());
            System.out.println(gexResp.getId());
            System.out.println(gexResp.getFields());
            System.out.println(gexResp.getField("name").getValue());
            System.out.println(gexResp.getSource());

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // on shutdown
            node.close();
        }

    }
}

```

还是`OperationThreaded`这个参数, 先记着. 
