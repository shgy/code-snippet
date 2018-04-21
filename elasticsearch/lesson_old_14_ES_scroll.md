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



