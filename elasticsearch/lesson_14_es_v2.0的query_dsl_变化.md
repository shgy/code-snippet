1. Queries and filters merged
   Query和Filter本来就是同一种事物，只是放置的环境不同而已。所以这里定义了Query context和 Filter context. 

2. terms query/fliter 废弃了
   ...

3. or 和 and 统一到bool下面了
   这个没啥说的

4. filtered query 使用 bool query 替代了
  这样的调整， 使得逻辑更清晰了。 至于有没有跟简洁了，得后面看。

5. Filter auto-caching
   
   在Filter context中的query有自动缓存的功能。关于`index.auto_expand_replicas` 这个没有弄明白是干嘛的。

6. Numeric queries使用idf来打分

   以前数值字段是不参与打分的。现在统一起来了。这个可以深入研究一下是怎么打分的。

7. fuzziness and fuzzy-like-this
   
   fuzzy matching 以前会对fuzzy的后选项一一打分，来衡量拼写错误的可能性。现在把IDF信息也融合进去了。这应该会使得fuzzy query 更加智能。
   得用一下才知道

8. more like this 
   
   mlt api 和`mlt_field`都废弃了。 使用`more_like_this` query 替代。
  
9. limit filter 废弃
   使用`terminate_after`代替

10. 自定义Query可以通过`IndicesQueriesModule#addQuery(Class<? extends QueryParser>)`方法添加， 其他的通道堵死了。

这些改变都朝着一个统一的方向： 更内聚， 更整齐划一。

  




   
