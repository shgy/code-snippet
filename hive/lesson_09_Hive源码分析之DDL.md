Hive使用的HQL与MySQL的SQL最为接近, 学习Hive, 打交道最多的也是HQL. 因此决定从分析HQL入手来理解Hive的源码.
SQL语言一共分为四大类: 数据查询语言DQL, 数据操纵语言DML, 数据定义语言DDL和数据控制语言DCL.

DQL的典型关键字有: Select
DML的典型关键字有: Insert/Update/delete
DDL的典型关键字有: Create Table/View/Index/Cluster| Show databases
DCL的典型关键字有: Grant/Rollback/commit

通常进入Hive后, 我们需要了解Hive中有哪些库, 库中有哪些表? 通常用到的语句如下:
```
show databases like "aa"
show tables in database like "aa"
show partitions table_name;
```

这些语句在Hive内部是如何执行的 ? 
`set hive.cli.print.current.db=true`则会在命令行中显示出当前所在的数据库.

以`show databases`为例:

Hive使用antlr来解析Hive的SQL, 然后将执行完成后的结果存储到/tmp目录下, 最后读取出来,展示到命令行界面.
```
/tmp/hive-shgy/46cd0336-a386-404d-9f42-8f922ba66f54/hive_2016-10-28_13-14-40_752_8180071189618217627-1$ cat -- -local-10000 
default
test
```

Parser                将SQL转换成抽象语法树
Semantic Analyzer     将抽象语法树转换成查询块
Logic Plan Generator  将查询块转换成逻辑查询计划

---- 2016-11-04

为了能够理解Hive是如何使用antlr的, 花了3天时间学习了antlr4的基础知识, 参考了<The Definitive ANTLR 4 Reference, 2nd Edition>
虽然是英文版, 学习曲线却相当平缓. 看了几天后, 我已经能大致明白Hive中的各个.g文件了.

---- 2016-11-08
理解Hive, 一定要抓住这三句话:
```
• 编译器将Hive SQL 转换成一组操作符(Operator)
• 操作符是Hive的最小处理单元
• 每个操作符处理代表一道HDFS操作或MapReduce作业
```
通过DEBUG跟踪`show databases`可以看到, Hive的执行整体上分为3步:

1. 编译SQL语句` org.apache.hadoop.hive.ql.Driver.QueryStat.compile(String,Boolean) # line 363`

2. 执行SQL语句` org.apache.hadoop.hive.ql.Driver.execute() #line 1304`

3. 输出返回结果` org.apache.hadoop.hive.ql.Driver.getResults(List res) #line 1684`

execute()的具体细节:
```
0. org.apache.hadoop.hive.ql.Driver.execute()
1. org.apache.hadoop.hive.ql.Driver.launchTask()
2. org.apache.hadoop.hive.ql.exec.TaskRunner.runSequential()
3. org.apache.hadoop.hive.ql.exec.Task.executeTask()
4. org.apache.hadoop.hive.ql.exec.DDLTask.execute()
5. org.apache.hadoop.hive.ql.exec.DDLTask.showDatabases() # 写入到文件中
```
getResults的具体细节
```
1. org.apache.hadoop.hive.ql.Driver.getResults()
2. org.apache.hadoop.hive.ql.exec.FetchTask.fetch() # 从文件中读取出数据
3. org.apache.hadoop.hive.ql.exec.FetchOperator.pushRow()
4. org.apache.hadoop.hive.ql.exec.ListSinkOperator.processOp()
5. org.apache.hadoop.hive.ql.exec.DefaultFetchFormatter.convert()
6. org.apache.hadoop.hive.serde2.DelimitedJSONSerDe.serialize()
```

通过DEBUG跟踪`explain show databases` 可以理解到Hive各个任务的依赖关系
```
STAGE DEPENDENCIES:
  Stage-0 is a root stage
  Stage-1 depends on stages: Stage-0

STAGE PLANS:
  Stage: Stage-0
      Show Databases Operator:
        Show Databases

  Stage: Stage-1
    Fetch Operator
      limit: -1
      Processor Tree:
        ListSink
```
可以看到,任务一共分成两个阶段: Stage-0和Stage-1.
我关注的是:
Stage-0的`Show Databases Operator` 和 Stage-1的`Fetch Operator`在哪里定义的呢
```
  # org.apache.hadoop.hive.ql.plan.DDLWork
  @Explain(displayName = "Show Databases Operator")
  public ShowDatabasesDesc getShowDatabasesDesc() {
    return showDatabasesDesc;
  }
```
和
```
org.apache.hadoop.hive.ql.plan
@Explain(displayName = "Fetch Operator")
public class FetchWork implements Serializable {
....
}
```

为什么这里有一个Fetch操作呢? 尽管最后的结果会展示到控制台,但是hive首先把查询结果存储到临时文件中, 然后通过
"Fetch Operator"任务读取出来.


问题2: 在DEBUG源码的过程中, 有FetchOperator/FetchWork/FetchTask三个类, 它们的关系是什么样的?
```
FetchOperator
└── FetchWork
    └── FetchTask
```
顺便扩展一下:

ListSinkOperator: 从FetchOperator读取的到row, 通过操作树处理, 最终达到这里.
SelectOperator: 对应到select关键字  主要功能是从每行的数据中解析出各个Field的值, 这关系到Hive的存储格式.
FilterOperator: 对应到where 关键字  主要功能是过虑数据.
TableSacnOperator: 对应到from 关键字

看来, 除了FetchOperator具有Operator/Work/Task这样的结构, 其它的是没有的.


`explain extended show databases`:
```
# execute()阶段, 依然是写入到文件中
0. org.apache.hadoop.hive.ql.Driver.execute()
1. org.apache.hadoop.hive.ql.Driver.launchTask()
2. org.apache.hadoop.hive.ql.exec.TaskRunner.runSequential()
3. org.apache.hadoop.hive.ql.exec.Task.executeTask()
4. org.apache.hadoop.hive.ql.exec.ExplainTask.execute()
5. org.apache.hadoop.hive.ql.exec.ExplainTask.getJSONPlan()
================================================================
# getResults()阶段
使用Utilities.readColumn()直接读取文件, 而没有FetchTask.
```

`select line from test.tab1;`
```
# execute()阶段: 没有任务
================================================================
#  getResults()阶段
1. org.apache.hadoop.hive.ql.Driver.getResults()
2. org.apache.hadoop.hive.ql.exec.FetchTask.fetch() # 从文件中读取出数据
3. org.apache.hadoop.hive.ql.exec.FetchOperator.pushRow()
4. org.apache.hadoop.hive.ql.exec.TableScanOperator.processOp()
5. org.apache.hadoop.hive.ql.exec.Operator.forward()
6. org.apache.hadoop.hive.ql.exec.SelectOperator.processOp()
       org.apache.hadoop.hive.ql.exec.ExprNodeEvaluator.evaluate()
       org.apache.hadoop.hive.ql.exec.ExprNodeColumnEvaluator._evaluate()
7. org.apache.hadoop.hive.ql.exec.ListSinkOperator.processOp()
```
分析: queryPlan维护了一个Operator Tree, 对于每一行数据. 按照深度优先的遍历方式来调用Tree中的每一个Operator

执行流程:
1. 从MySQL中找到表名对应的数据位置.
2. 使用Hadoop的API从HDFS读取数据.
2. 依次处理每一行的数据.


问题:
   1. Hive如何解析antlr生成的AST, 生成queryPlan ? 对antlr不熟悉
   2. Hive如何执行复杂的MapReduce任务 ?           对Hadoop不熟悉










