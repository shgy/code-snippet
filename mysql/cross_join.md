需求: 已知数据表中每天的营业额, 求:每天的历史(包含当天)营业额总数.
样本数据如下:
```
    a       , b
'2015-01-01', 1
'2015-01-02', 2
'2015-02-01', 3
'2015-02-02', 4
```
sql语句:
```
select a_dt, sum(b_n) from (select a.a a_dt, a.b a_n, b.a b_dt, b.b b_n from test a cross join test b where a.a >= b.a ) a
group by a_dt

'2015-01-01', '1'
'2015-01-02', '3'
'2015-02-01', '6'
'2015-02-02', '10'

```
即截至到2015-02-02, 一共有10块钱入账.
