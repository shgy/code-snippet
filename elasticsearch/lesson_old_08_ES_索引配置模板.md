```

curl -XPUT 'http://localhost:9200/hbaseindex' -d '{
    "settings": {
        "index": {
            "number_of_shards": 1,
            "number_of_replicas": 1,
            "analysis": {
                "analyzer": {
                    "jieba_search": {
                        "type": "jieba",
                        "seg_mode": "search",
                        "stop": true
                    },
                    "jieba_other": {
                        "type": "jieba",
                        "seg_mode": "other",
                        "stop": true
                    },
                    "jieba_index": {
                        "type": "jieba",
                        "seg_mode": "index",
                        "stop": true
                    },
                    "default":{
                        "type":"jieba"
                    }
                }
            }
        }
    },
    "mappings": {
        "qyxx": {
            "date_detection": false,
            "_source": {
                "enabled": false
            }
        }
    }
}'
```
