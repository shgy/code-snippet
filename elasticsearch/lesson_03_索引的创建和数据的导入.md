es在网上的归类是NoSQL 数据库。 既然是数据库,那么建表,导入数据,查询这些就是最基本的操作了.

1. 建库
```
curl -XPOST localhost:9200/cars -d '{
    "settings" : {
        "number_of_shards" : 1
    }
}'
```

2. 批量导入数据
```
curl -XPOST 'localhost:9200/cars/transactions/_bulk?pretty' -H 'Content-Type: application/json' -d'
{ "index": {}}
{ "price" : 10000, "color" : "red", "make" : "honda", "sold" : "2014-10-28" }
{ "index": {}}
{ "price" : 20000, "color" : "red", "make" : "honda", "sold" : "2014-11-05" }
{ "index": {}}
{ "price" : 30000, "color" : "green", "make" : "ford", "sold" : "2014-05-18" }
{ "index": {}}
{ "price" : 15000, "color" : "blue", "make" : "toyota", "sold" : "2014-07-02" }
{ "index": {}}
{ "price" : 12000, "color" : "green", "make" : "toyota", "sold" : "2014-08-19" }
{ "index": {}}
{ "price" : 20000, "color" : "red", "make" : "honda", "sold" : "2014-11-05" }
{ "index": {}}
{ "price" : 80000, "color" : "red", "make" : "bmw", "sold" : "2014-01-01" }
{ "index": {}}
{ "price" : 25000, "color" : "blue", "make" : "ford", "sold" : "2014-02-12" }
'
```

3. 使用聚合查询数据
```
curl -XGET 'localhost:9200/cars/transactions/_search?pretty' -H 'Content-Type: application/json' -d'
{
    "size" : 0,
    "aggs" : { 
        "popular_colors" : { 
            "terms" : { 
              "field" : "color"
            }
        }
    }
}
'
```

这个就是Aggregations中最常见的一种聚合方式: `Terms Aggregation`
上例中, 给的是订单数据, 统计的是所有订单中, 每种颜色汽车的销售额. 
相当于SQL的
```
  select color, sum(1) order_cnt from transactions group by order;
```
实际上, 在做数据分析时, 这个只是最基本的数据需求. 在这个基础上, 运营人员还会有更多的需求,
比如 按天分隔, 按周分隔, 只取某个地区的订单, 取某批用户的订单(看活动效果)...

在关系型数据库中, 使用SQL join能够很好地解决这个问题, 在ES中, 如何解决呢?


参考: https://www.elastic.co/guide/cn/elasticsearch/guide/current/_aggregation_test_drive.html