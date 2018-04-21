```

org.elasticsearch.common.breaker.CircuitBreakingException: [FIELDDATA] Data too large, data for [@timestamp] would be larger than limit of [622775500/593.9mb]

```

解决方案：

```

curl -XPUT localhost:9200/_cluster/settings -d '{
  "persistent" : {
    "indices.breaker.fielddata.limit" : "70%" 
  }
}'
```
