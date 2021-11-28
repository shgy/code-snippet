SemiJoin又称为半连接，只获取一个表中的字段。
在Presto中，SemiJoin的实现思路比较有意思，类似于Hash的Join算法。即in(subquery)语法的执行分成两个步骤：
第一步：subquery的字段生成一个Set。
第二步：遍历probe表，执行contains操作，看字段是否在set中。


这里我最开始有两个误解：
误解1：两个步骤是分布到不同的节点执行(我想到是处理成set后传输，比直接传输原数据列会节约带宽，但是没想到set反序列化的问题)
误解2：:以为步骤2是直接过滤，没想到是新增了一个boolean字段用于标识是否匹配。

这里其实可以从sql语法反推的。sql语法支持in和not in。 这里in和not in的功能没有集成到HashSemiJoinOperator中，
而是集成到了FilterAndProjectOperator中。

也就是说，in(subquery)语法，在生成operator时拆分成了两个opeator协作执行。


理解了semijoin的实现思路，就可以着手改造成支持bitmap的实现了。