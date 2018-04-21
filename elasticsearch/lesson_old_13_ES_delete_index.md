1 只删除数据
```
curl -XDELETE 'http://ubuntu-shgy:9200/dp_test_3/qyxx/_query' -d '{

    "query" : {

         "match_all" : {}

    }

}'

```
2 删除mapping
```
curl -XDELETE 'http://ubuntu-shgy:9200/dp_test_3/qyxx/'
```


3 elasticsearch只返回指定的字段

```



```
