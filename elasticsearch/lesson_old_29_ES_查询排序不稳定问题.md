查询时添加preference参数即可.

参考:https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-preference.html

For instance, use the user’s session ID to ensure consistent ordering of results for the user

```

curl localhost:9200/_search?preference=xyzabc123 -d '

{

    "query": {

        "match": {

            "title": "elasticsearch"

        }

    }

}'

```
