当一个mapreduce任务完成， 我们最关注的就是跑出来的结果数据存在哪里。
就wordcount而言， 结果数据存储在下面代码设置的目录中。
``` 
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
```

结果目录中有两类文件：
其一是 数据文件， 可能有多个通常名字前缀为`part`； 
其一是 标记文件， 标记任务成功。通常名字为`_SUCCESS`。

这两类文件对应着OutputFormat类的两个方法: `getRecordWriter()`和`getOutputCommitter()`
我们学 InputFormat的时候，也是这样入手的。


先见识一下这个类`getOutputCommitter()`
``` 
OutputCommitter describes the commit of task output for a Map-Reduce job.

The Map-Reduce framework relies on the OutputCommitter of the job to:

Setup the job during initialization. For example, create the temporary output directory for the job 
during the initialization of the job.

Cleanup the job after the job completion. For example, remove the temporary output directory after 
the job completion.

Setup the task temporary output.

Check whether a task needs a commit. This is to avoid the commit procedure if a task does not need commit.
Commit of the task output.

Discard the task commit.

The methods in this class can be called from several different processes and from several different contexts.
It is important to know which process and which context each is called from. Each method should be marked
accordingly in its documentation. It is also important to note that not all methods are guaranteed to be 
called once and only once. If a method is not guaranteed to have this property the output committer needs 
to handle this appropriately. Also note it will only be in rare situations where they may be called 
multiple times for the same task.
```
看来这个类 很重要， job初始化的时候有他， job完成的时候也有他。OutputCommitter 的职责贯穿了整个map-reduce任务执行过程。
我们目前只从`_SUCCESS`文件入手， 看OutputCommitter的工作流程。

以wordcount为例：

1. reduce任务完成后， 产生的结果散落在各个reduce task的工作目录中。需要按照用户的配置， 将结果文件聚合到指定位置。
2. 当前job依赖的配置文件， 各个任务的工作目录等需要清理。 要还集群一个干净，不能留下垃圾。

这些任务都是在 FileOutputCommitter.commitJob() 方法中完成的。一共按顺序分成3步：
1. 聚合结果数据到用户指定目录中。
2. 清理任务执行过程产生的垃圾（删除_temporary目录）。
3. 创建`_SUCCESS`文件， 标示任务成功执行。

了解到commitJob()过程中，会清理`_termporary`目录， 那么`_termporary`目录是怎么来的呢？ 
这就需要了解 FileOutputCommitter.setupJob() 方法了。
在map 任务执行前， setupJob()方法会创建工作目录`jobAttemptPath`， 
这个目录由 _temporary 和 `mapreduce.job.application.attempt.id` 的参数合成。


接下来了解一下`part-r-*****`数据文件是怎么来的了。
 
说`getRecordWriter`, reduce任务在初始化时， 会在工作目录创建`part-r-*****`文件。

在此之前， 我们先了解一下这3个类`JobID`, `TaskID`, `TaskAttemptID`.
```
JobID represents the immutable and unique identifier for the job. 
JobID consists of two parts. First part represents the jobtracker identifier, 
so that jobID to jobtracker map is defined. For cluster setup this string is 
the jobtracker start time, for local setting, it is "local" and a random number.
 Second part of the JobID is the job number. 
An example JobID is : job_200707121733_0003 , which represents the third job 
running at the jobtracker started at 200707121733.

TaskID represents the immutable and unique identifier for a Map or Reduce Task. 
Each TaskID encompasses multiple attempts made to execute the Map or Reduce Task, 
each of which are uniquely indentified by their TaskAttemptID. TaskID consists of 3 parts. 
First part is the JobID, that this TaskInProgress belongs to. 
Second part of the TaskID is either 'm' or 'r' representing whether the task is a map task 
or a reduce task. And the third part is the task number. 
An example TaskID is : task_200707121733_0003_m_000005 , which represents the fifth map task 
in the third job running at the jobtracker started at 200707121733.


TaskAttemptID represents the immutable and unique identifier for a task attempt. 
Each task attempt is one particular 
instance of a Map or Reduce Task identified by its TaskID. TaskAttemptID consists of 2 parts. 
First part is the TaskID, that this TaskAttemptID belongs to. Second part is the task attempt number. 
An example TaskAttemptID is : attempt_200707121733_0003_m_000005_0 , 
which represents the zeroth task attempt for the fifth map task in the third job running at the 
jobtracker started at 200707121733.
```

如
```
file:/home/shgy/tmp/out/_temporary/0/_temporary/attempt_local1806003000_0001_r_000000_0/part-r-00000
```
这个文件的拼接规则是啥呢？ 
其中：
`file:/home/shgy/tmp/out`是用户设置的输出目录。
`_temporary/0/_temporary/` 两层`_temporary` 目录的原因在于job需要temporary, task也需要temporary。
`attempt_local1479072795_0001_r_000000_0` 是当前TaskAttempt的唯一ID。
`part-r-00000` 是定义的输出文件。 

以`part-r-00000`为例：
1. `mapreduce.output.basename`的默认值是`part`,
2. reduce任务的partition是0, 格式化时默认保留5位数值。
3. reduce任务的代表字符是r
``` 
charToTypeMap.put('m', TaskType.MAP);
charToTypeMap.put('r', TaskType.REDUCE);
charToTypeMap.put('s', TaskType.JOB_SETUP);
charToTypeMap.put('c', TaskType.JOB_CLEANUP);
charToTypeMap.put('t', TaskType.TASK_CLEANUP);
```
所以， `part-r-00000` = 'part-'(前缀) + 'r'(任务类型) + '00000'(分区)

以`attempt_local1479072795_0001_r_000000_0`为例：
1. attempt 表示 TaskAttemptID的前缀。
2. `local1479072795_0001`是JobID的命名规则： `JobID("local" + randid, ++jobid)`， 这里面有个随机数。
3. `r_000000` 是TaskID的命名规则， 表示类型为reduce的0号Task.
4. 最后的`0`表示任务的第0次尝试。


做个实验， 修改`mapreduce.output.basename`的值， 尽管暂时看不到实际意义：
```
conf.set("mapreduce.output.basename","my-own");
```
最后在输出结果中, 可以看到文件前缀已由`part`变成了`my-own`。

由`LineRecordWriter`将最后的结果写入到part文件中。

那么问题又来了：
Reduce的输入怎么来的 ？
Map的输出怎么处理 ？
这两者如何衔接？