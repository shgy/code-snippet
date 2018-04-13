 比如，有需求需要计算下单到现在有几个月。 
 1~30天记为1个月； 31~60天记为两个月。。。
 两种方式：
 ```
 select ceil(datediff('${nowdate}', order_date)/30)
 select ceil(months_between('${nowdate}', order_date))
 ```
