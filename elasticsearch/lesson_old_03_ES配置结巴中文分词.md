1 在es的plugins目录中添加jieba的jar包：
jieba
├── commons-lang3-3.3.2.jar

├── elasticsearch-analysis-jieba-0.0.4.jar

└── jieba-analysis-1.0.2.jar

2 在es的config目录中添加jieba的词典：
├── elasticsearch.yml
├── jieba
│   ├── sougou.dict

│   ├── stopwords.txt

│   └── user.dict

└── logging.yml

3 配置mappings如下：
```shell

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
