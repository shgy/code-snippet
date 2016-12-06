数据如下:
```
a, 1
b, null
```
查询如下:
```
select * from tab where c1 != 1
```
结果为空, 这是因为, NULL 表示一个未知的值, 正确的做法是
```
select * from tab where COALESCE(c1,0) != 1
```
