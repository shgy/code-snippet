参考: http://www.joefkelley.com/727/
需求: 查找年龄最大的用户的姓名 或者 充值金额最多的用户的姓名...
一般使用嵌套查询, Hive提供了另一种方式:

```
select store_id, max(
    named_struct(
        'amount_spent', amount_spent,
        'customer_id', customer_id
    )
).customer_id
from customer_spending
group by store_id
```
