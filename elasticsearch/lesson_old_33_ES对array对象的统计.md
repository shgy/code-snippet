```

conf/elasticsearch.yml

script.inline: true

script.indexed: true



curl -XPUT 'localhost:9200/dp_test_250/test/4' -d '{

   "users": ["usera","user2"]

}'



{

  "aggs": {

    "user_sum": {

      "sum": {

        "script": "doc['users'].size()"

      }

    }

  }

}

```
