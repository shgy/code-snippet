Hive的count()函数格式如下:
```
count(*)                     - Returns the total number of retrieved rows, including rows containing NULL values.
count(expr)                  - Returns the number of rows for which the supplied expression is non-NULL.
count(DISTINCT expr[, expr]) - Returns the number of rows for which the supplied expression(s) are unique and non-NULL. Execution of this can be optimized with hive.optimize.distinct.rewrite.
```

我比较关注`count(expr)` count中支持表达式, 那可以做的事情就多了.例如有如下的数据表:
```
# table_name user
-----------------------
| province | user_id  | 
-----------------------
|  北京    | 10       |
-----------------------
|  北京    | 11       |
-----------------------
|  上海    | 15       |
-----------------------
|  天津    | 5        |
-----------------------
|  重庆    | 2        |
-----------------------
|  南京    | 7        |
   ........
```
我希望统计`华东`, `东北`, `西北`, `华南`, `西南`,`华中` 6个地区的用户数, 怎么做呢?
```
select 
   count(distinct if(province in ('北京' ... ), user_id , null) huabei_sales,
   count(distinct if(province in ('上海' ... ), user_id , null) huadong_sales,
   ...
   from user;
```


