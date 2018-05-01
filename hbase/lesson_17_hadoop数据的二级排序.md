很多时候, 我们处理的数据并不是像wordcount那么简单.比如订单数据
```
emp1,1月,100
emp1,3月,100
emp1,4月,100
emp3,2月,100
```
比如我们希望输出的结果是:
```
emp1: 100,0   ,100,100
emp2: 0,  100,0   ,0
```
即每个员工的销售额1行, 依次从1月到4月, 没有的月份补0
这就需要每个员工的销售额按照月份排序

参考:
https://vangjee.wordpress.com/2012/03/20/secondary-sorting-aka-sorting-values-in-hadoops-mapreduce-programming-paradigm/

