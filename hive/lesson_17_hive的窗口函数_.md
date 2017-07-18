RANK()	        返回数据项在分组中的排名，排名相等会在名次中留下空位
DENSE_RANK()	返回数据项在分组中的排名，排名相等会在名次中不会留下空位
NTILE()    	    返回n分片后的值
ROW_NUMBER()	为每条记录返回一个数字
```
SELECT
column_name,
RANK() OVER （ORDER BY column_name DESC） AS rank,
DENSE_RANK() OVER （ORDER BY SUM(column_name) DESC） AS dense_rank
FROM table_name
```
注意: order by 时，desc NULL 值排在首位，ASC时NULL值排在末尾
可以通过NULLS LAST、NULLS FIRST 控制
```
RANK() OVER (ORDER BY column_name DESC NULLS LAST)

partition by 用于分组

RANK() OVER(PARTITION BY month ORDER BY column_name DESC)
```

Hive的增强聚合： grouping sets(), rollup, cube, GROUPING__ID
先说grouping sets().
eg: 需求， 假如有一份销售数据，字段如下：
```
table:  sale
fields: 地区(area)   员工(emp)  月份(mon)  销售额(amount)
```
如果要统计 每个地区的销售额 以及 每个地区每个月份的销售额， 通常需要两条sql
```
   select area, sum(amount) from sale group by area
   和
   select area, mon, sum(amount) from sale group by area
```
如果使用group set, 则 一条sql就够了
```
   select area, mon, sum(amount) from sale group by area, mon grouping set((area,mon),area)
```
grouping set中注明了分组信息。

with cube和 with rollup
cube 就列出所有的组合， rollup 列出前缀。
```
GROUP BY a, b, c WITH CUBE is equivalent to
GROUP BY a, b, c GROUPING SETS ( (a, b, c), (a, b), (b, c), (a, c), (a), (b), (c), ( )).
```
```
GROUP BY a, b, c, WITH ROLLUP is equivalent to GROUP BY a, b, c GROUPING SETS ( (a, b, c), (a, b), (a), ( )).
```
了解了with cube 和with rollup的功能， grouping__id也就好理解了。
既如果group by的字段中有null值， 使用grouping sets时， 就有可能出现如下的情况
```
select key1,key2,sum(val) from table group by key1,key2 grouping sets((key1,key2),key1)
-------------------------------------
key1 key2 sum(val)
1  NULL 2
1  NULL 1
```
grouping__id就是用于区分key1,key2所属的分组。
如何根据grouping__id的值判断是哪个分组呢？
```
SELECT
emp,mon, grouping__id, sum(val)
FROM (
   select stack(3, 'e1', 'm1',1,'e2', 'm2',2,'e3', 'm3',3) as (emp, mon, val)
)t
GROUP BY emp, mon with cube;
```
()        grouping__id = 0
emp       grouping__id = 1
mon       grouping__id = 2
(emp,mon) grouping__id = 3

NTILE
按层次查询，如一年中，统计出工资前1/5之的人员的名单，使用NTILE分析函数,把所有工资分为5份，为1的哪一份就是我们想要的结果：
```
select empno,ename,sum(sal),ntile(5) over (order by sum(sal) desc nulls last) til
from emp group by empno,ename;
```


二、窗口函数
可以计算一定范围内、一定值域内、或者一段时间内的累积和以及移动平均值等。
可以结合聚集函数SUM() 、AVG() 等使用。

可以结合FIRST_VALUE() 和LAST_VALUE()，返回窗口的第一个和最后一个值

（1）计算累计和

eg：统计1-12月的累积销量，即1月为1月份的值，2月为1.2月份值的和，3月为123月份的和，12月为1-12月份值的和

```
SELECT
month,SUM(amount) month_amount,
SUM( SUM(amount)) OVER (ORDER BY month ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW) AS cumulative_amount
FROM table_name
GROUP BY month
ORDER BY month;
```
 其中：
SUM( SUM(amount)) 内部的SUM(amount)为需要累加的值，在上述可以换为 month_amount
ORDER BY month 按月份对查询读取的记录进行排序，就是窗口范围内的排序
ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW 定义起点和终点，UNBOUNDED PRECEDING 为起点，表明从第一行开始, CURRENT ROW为默认值，就是这一句等价于：
ROWS UNBOUNDED PRECEDING
PRECEDING：在前 N 行的意思。
FOLLOWING：在后 N 行的意思。

计算前3个月之间的和

```
SUM( SUM(amount)) OVER (ORDER BY month ROWS BETWEEN 3 PRECEDING AND CURRENT ROW) AS cumulative_amount
```
 也可以
```
SUM( SUM(amount)) OVER (ORDER BY month 3 PRECENDING) AS cumulative_amount
```
 前后一个月之间的和
```
SUM( SUM(amount)) OVER (ORDER BY month ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) AS cumulative_amount
```
窗体第一条和最后一条的值
```
FIRST_VALUE(SUM(amount)) OVER (ORDER BY month ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) AS xxxx;

LAST_VALUE(SUM(amount)) OVER (ORDER BY month ROWS BETWEEN 1 PRECEDING AND 1 FOLLOWING) AS xxxx;
 ```
三、LAG、LEAD
获得相对于当前记录指定距离的那条记录的数据
LAG()为向前、LEAD()为向后
```
LAG(column_name1,1) OVER(ORDER BY column_name2)

LEAG(column_name1,1) OVER(ORDER BY column_name2)
```
这样就获得前一条、后一条的数据

四、FIRST、LAST
获得一个排序分组中的第一个值和组后一个值。可以与分组函数结合
```
SELECT
MIN(month) KEEP(DENSE_RANK FIRST ORDER BY SUM(amount)) AS highest_sales_month,
MIN(month) KEEP(DENSE_RANK LAST ORDER BY SUM(amount)) AS lows_sales_month
FROM table_name
GROUP BY month
ORDER BY month;
```
这样就可以求得一年中销量最高和最低的月份。
输出的是月份，但是用SUM(amount)来判断。


构造样例数据表: 员工,日期,金额 三个字段。 假设这是员工的销售业绩表：
```
drop table if exists temporarydb.tmp_sales_0714;
create temporary table if not exists temporarydb.tmp_sales_0714
as
SELECT
emp,
date_add('2017-07-01', cast(pos+rand()*3 as int)) date_id,
cast(1+rand()*10 as bigint) amt
FROM (
   select stack(3, 'e1', 1,'e2', 2,'e3',3) as (emp, amt)
)t lateral view posexplode(split(repeat('1',4),'1')) dd as pos, val
```

如果需要数据上下错位， 这个就很有意思了。 这在处理漏斗图/算增长率上可能会有用。
```
select emp,date_id, amt,
lag(amt) over( partition by emp order by date_id),
lead(amt) over( partition by emp order by date_id)
from temporarydb.tmp_sales_0714
order by emp, date_id
```



参考: http://yugouai.iteye.com/blog/1908121
https://cwiki.apache.org/confluence/display/Hive/Enhanced+Aggregation%2C+Cube%2C+Grouping+and+Rollup