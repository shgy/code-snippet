在删除数据量过亿的索引时， ES的返回值并不是`{"acknowledged":true}`
```
curl -XPUT 'localhost:9200/customer'
curl -XPUT 'localhost:9200/customer/external/1' -d '
{
  "name": "John Doe"
}'
curl -XDELETE 'localhost:9200/customer'
```




