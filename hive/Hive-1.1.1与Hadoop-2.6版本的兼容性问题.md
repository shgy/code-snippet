hive使用hadoop进行查询的时候, 报了一个异常,搜索了一下, 得到的解决方案如下:

```
java.lang.NoSuchMethodError: org.apache.hadoop.hive.ql.ppd.ExprWalkerInfo.getConvertedNode(Lorg/apache/hadoop/hive/ql/lib/Node;)Lorg/apache/hadoop/hive/ql/plan/ExprNodeDesc;
at org.apache.hadoop.hive.ql.ppd.ExprWalkerProcFactory$GenericFuncExprProcessor.process(ExprWalkerProcFactory.java:176)
set hive.optimize.ppd=false;
```
