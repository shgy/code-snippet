hive一共有3中自定义函数: UDF, UDAF, UDTF. 使用频繁程度依次递减。

UDAF 就是类似与sum, max, min 这种聚合函数。 有什么场景需要自定义聚合函数呢？

比如： 对分组后的某一列数值取topN. 如果有自定义的聚合函数，这个SQL就不用写得那么复杂。
比如 生成bitmap.

首先， 学习UDAF需要理解Hive SQL的执行原理， 也就是说Hive是怎么执行mr程序的。有了这个基础，
再来理解 UDAF就比较简单了。

接下来，参考类似与`sum, min, max, collect_set`这类内置的udaf函数的实现方式，依样化葫芦。


参考： https://letsdobigdata.wordpress.com/2016/03/02/writing-hive-udf-and-udaf/




