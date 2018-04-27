写在shell脚本中有利于动态扩展.
```
$ cat curl_demo.sh 
suffix=`date +"%Y%m%d%H%M%S"`
url="localhost:9200/hbaseindex-${suffix}"
echo $url
curl -XPUT "${url}" -d '{
    "settings": {
        "index": {
            "number_of_shards": 1,
            "number_of_replicas": 1
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
