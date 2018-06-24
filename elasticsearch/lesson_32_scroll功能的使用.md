ES的scroll用到了很多地方：1 elasticsearch-hadoop中数据的查询；2  elasticsearch-hadoop中数据的查询
python 的一段demo如下：
```

# -*- coding: utf-8 -*-

# Created by 'shgy' on '16-3-2'

from elasticsearch import Elasticsearch





read_es = Elasticsearch(hosts=[{"host":"dataom3", "port":9200}])

write_es = Elasticsearch(hosts=[{"host":"ubuntu-shgy", "port":9200}])

page = read_es.search(

  index = 'dp_test',

  doc_type = 'qyxx',

  scroll = '2m',

  search_type = 'scan',

  size = 1000,

  body = {

     "query" : {

        "match_all" : {}

     }

    })



sid = page['_scroll_id']

scroll_size = page['hits']['total']



import json



# Start scrolling

while (scroll_size > 0):

    print "Scrolling..."

    page = read_es.scroll(scroll_id = sid, scroll = '2m')

    # Update the scroll ID

    sid = page['_scroll_id']

    # Get the number of results that we returned in the last scroll

    # scroll_size = len(page['hits']['hits'])

    # print "scroll size: " + str(scroll_size)

    bodyList = []

    for esdoc in page['hits']['hits']:

        source = esdoc['_source']

        id = esdoc['_id']

        bodyList.append('{ "index" : { "_index" : "dp_test_2", "_type" : "qyxx", "_id" : "%s" } }' % id)

        bodyList.append(json.dumps(source, ensure_ascii=False))

    print 'start bulk'

    es_resp = write_es.bulk(index='dp_test_2', doc_type='qyxx', body='\n'.join(bodyList))


```
Java的demo如下:
maven dependencies
```
  <dependencies>
    <dependency>
      <groupId>org.elasticsearch</groupId>
      <artifactId>elasticsearch</artifactId>
      <version>${es.version}</version>
    </dependency>
    <dependency>
      <groupId>org.elasticsearch</groupId>
      <artifactId>jna</artifactId>
      <version>4.4.0</version>
    </dependency>
    <dependency>
      <groupId>commons-cli</groupId>
      <artifactId>commons-cli</artifactId>
      <version>1.4</version>
    </dependency>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>4.11</version>
      <scope>test</scope>
    </dependency>

  </dependencies>
```
Java code
```
package com.sgh;


import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;


import java.net.InetAddress;
import java.net.UnknownHostException;

public class ScrollDemo {
    public static void main(String[] args) throws UnknownHostException {

        Settings settings = Settings.settingsBuilder()
                .put("cluster.name", "elasticsearch").build();
        Client client = TransportClient.builder().settings(settings).build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("localhost"), 9300));
        try{
            ClusterStatsResponse resp = client.admin().cluster().clusterStats(new ClusterStatsRequest()).actionGet();
            System.out.println(resp.getStatus());


            QueryBuilder qb = QueryBuilders.matchAllQuery();

            SearchResponse scrollResp = client.prepareSearch("twitter")
                    .setScroll(new TimeValue(60000))
                    .setQuery(qb)
                    .setSize(100).execute().actionGet(); //100 hits per shard will be returned for each scroll
            //Scroll until no hits are returned
            while (true) {
                System.out.println(scrollResp);
                for (SearchHit hit : scrollResp.getHits().getHits()) {
                    //Handle the hit...

                }
                scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
                //Break condition: No hits are returned
                if (scrollResp.getHits().getHits().length == 0) {
                    break;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // on shutdown
            client.close();
        }


    }
}

```

不妨再扩展一下： 
1. es能同时支持多少的scroll?
2. scroll对es的影响有多大?
