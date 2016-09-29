业务上可能存在这样的需求: 统计每天销量前10的地区, 在hive中怎么用sql解决呢?
```
select  a.*
from
 (
    select dt,province, rate, row_number() over (partition by dt order by rate desc ) rank
       from default.sale_table group by dt,province, rate
)a
where a.rank<=10
```
