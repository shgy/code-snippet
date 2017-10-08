OutputFormat提供了3个函数：`getRecordWriter`，`checkOutputSpecs`，`getOutputCommitter`

先挑软柿子捏： `checkOutputSpecs` 功能为检测输出目录是否配置；如果配置了，是否为空。

OutputFormat输出的是最终的结果， 即一个或者多个ReduceTask的执行结果输出到一个目录中。
输出的过程是怎样的呢?

OutputCommitter类：
1.  setupJob
2.  setupTask
3.  commitTask
4.  commitTask

每个任务有其单独的工作空间， 最后在commitTask中合并。




