1. Min doc count defaults to zero
 histogram 和 `date_histogram`聚合的默认值变成0, 而不是1.

2. Timezone for date field
 这个已经提过了， timezone的标准化

3. Time zones and offsets
 使用offset选项代替`pre_offset`和`post_offset`. 
 使用`time_zone`代替`pre_zone`和`post_zone`参数。
 这个后面得实战一下， 看看这个功能是怎么用的

4. including/excluding terms
 terms聚合使用跟`regexp queries`一样的语法了。

5. boolean fields 
 boolean fields 的聚合会返回0和1, 字符串值返回`true`和`false`

6. Java aggregation classes
 `date_histogram`聚合返回`Histogram`对象， `DateHistogram`类已经移除。
 同样地`date_range, ipv4_range, geo_distance`聚合返回`Range`对象， IPV4Range, DateRange和GeoDistance类已经被移除。
 这样改的目的也很简单， 对外一致性，使用更简单方便

 MultiBucketAggregationg接口也做了一些修改：
 getKey() 返回Object
 getKeyAsString() 新增
 getKeyAsX() 移除
 getBucketAsKey(String) 移除，fIlters 和 terms 聚合除外。


聚合这块的东西，得多使用， 深入了解其功能。 没有大的变化。



  


