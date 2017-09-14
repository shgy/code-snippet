InputFormat describes the input-specification for a Map-Reduce job.

The Map-Reduce framework relies on the InputFormat of the job to:

Validate the input-specification of the job.
Split-up the input file(s) into logical InputSplits, each of which is then assigned to an 
individual Mapper.
Provide the RecordReader implementation to be used to glean input records from the logical 
InputSplit for processing by the Mapper.
The default behavior of file-based InputFormats, typically sub-classes of FileInputFormat, 
is to split the input into logical InputSplits based on the total size, in bytes, of the input files. 
However, the FileSystem blocksize of the input files is treated as an upper bound for input splits. 
A lower bound on the split size can be set via mapreduce.input.fileinputformat.split.minsize.

Clearly, logical splits based on input-size is insufficient for many applications since 
record boundaries are to respected. In such cases, the application has to also implement a 
RecordReader on whom lies the responsibility to respect record-boundaries and present a 
record-oriented view of the logical InputSplit to the individual task.

1. InputSplits是逻辑拆分。  用于确定MapTask的个数, 每个Split一个MapTask。

2. RecordReader用于从InputSplits中读取处理单元供 Mapper使用。


InputFormat是开放的第一个接口。用户可自定义getSplits和createRecordReader这两个方法，实现相关的业务逻辑。
比如从数据库中读取记录到Hadoop中， 从Kafka中读取记录到Hadoop中....

有许多的子类： ComposableInputFormat, CompositeInputFormat, DBInputFormat, FileInputFormat

MapReduce默认的InputFormat是TextInputFormat.

TextInputFormat是按行读取文件， 然后由MapTask处理。
这里就出现了一个需要解决的问题： 比如wordcount,假如恰好一个单词被分到了两个block中, 怎么办呢？
RecordReader就是来解决这个问题的。

如果是我， 处理该InputSplit的Reader, 如果读取到了该InputSplit的末尾， 但是并不是行末尾， 那么把该行读完。
即《深入Mapreduce架构设计与实现原理》中的 `每个InputSplit中的第一条不完整记录划给前一个InputSplit处理`

实际上，hadoop的做法是，在处理split时， 丢弃第一行记录即可。

``` LineRecordReader.initialize() 方法
// If this is not the first split, we always throw away first record
// because we always (except the last split) read one extra line in
// next() method.
if (start != 0) {
  start += in.readLine(new Text(), 0, maxBytesToConsume(start));
}
```

