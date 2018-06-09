1 `search_type=scan`废弃, `scroll` 查询已经取代它的功能。

2 `search_type=count`废弃, 设置`size=0`就OK了。

3 `from+size` 有窗口限制了， 默认是`10000`, 大于该值会抛出错误信息。

4  内嵌字段排序需要明确指定`nested_path`参数
5  MoreLikeThis的变更

6 `detect_noop`默认设置为true, 当设置为false时， es的操作就简单了
`update`=`delete/index` 不管有没有真的变更。

7 Optimize API被废除。新的`optimize`操作将使用新的Force Merge API

8 Queue size stats的返回值不再是`1k` 这种不利于编程的结果了。

9 `indices.fielddata.cache.expire` 废除

10 `thread pool type` 的变更被禁止 
   这种参数容易被误用， 对性能有起不了太大作用。


