需求如下：

1 所有入库的数据，Mapping必须存在。(对应MySQL => 即所有入库的数据，数据表必须存在)。

2 所有入库的数据，Maping中如果没有该字段，则自动忽略该字段， _source中也不存储该字段。

创建Mapping的配置方式如下：

```

curl -XPUT 'localhost:9200/dp_test_500/' -d '{

   "settings":{

        "index": {

            "number_of_shards": 1,

            "number_of_replicas": 1,

            "refresh_interval" : "60s",

            "mapper":  {"dynamic": false}

        }

   },

   "mappings":{

       "qyxx" : {

            "date_detection": false,

            "dynamic": false,

            "_source": {

                "includes": ["bbd_dotime", "bbd_uptime","hbase_rowkey","company_gis_lat","company_gis_lon","company_history_name","company_name","bbd_table","bbd_type","bbd_qyxx_id"]

            },

            "properties": {

                "bbd_dotime" : {"type" : "string", "index" : "not_analyzed" },

                "bbd_uptime" : {"type" : "string", "index" : "not_analyzed" },

                "hbase_rowkey" : {"type" : "string", "index" : "not_analyzed" },

                "company_gis_lat": {"type" : "float" },

                "company_gis_lon": {"type" : "float" },

                "company_history_name": {"type" : "string"},

                "company_name": {"type" : "string"},

                "bbd_table": { "type" : "string", "index" : "not_analyzed" },

                "bbd_type": { "type" : "string", "index" : "not_analyzed" },

                "bbd_qyxx_id": { "type" : "string", "index" : "not_analyzed" }

            }

       }

   }

}'

```

配置的重点在于：

```

 "mapper":  {"dynamic": false}  

```

表示，如果该type没有配置，则无法添加数据到集群。

```

 "dynamic": false,

"_source": {    "includes": ["bbd_dotime", "bbd_uptime","hbase_rowkey","company_gis_lat","company_gis_lon","company_history_name","company_name","bbd_table","bbd_type","bbd_qyxx_id"]

 },

```

表示只允许mapping中设置的字段添加到索引，并且_source中只显示includes中设置的字段。


