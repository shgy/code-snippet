Hive有三大核心组件:SerDe, MetaStore, QueryProcessor.
其中SerDe相当于Hive的文件系统, 例如Linux中有ext2, ext3等多种多样的文件系统, 满足不同应用场景的需求.
同样, Hive也内置了好几种不同的文件格式, 需要不同的SerDe来解析, 比如: avro, ORC, text, sequence, parquet等.
在Hive中,通常把数据存储成ORC格式, 以提高Hive的处理性能.

###Input阶段###
即将硬盘上的数据读取出来
首先, 使用InputFormat读取出一行记录.
然后, 使用SerDe.deserialize()将记录行解析出来. 这里可以使用懒操作.
然后, 通用调用SerDe.getObjectInspector() 得到 Objectspector()
然后, 结合ObjectInspector和反序列化后的对象就可以得到指定的数据.

为了方便理解Hive, 我自行编译了Hive.
```
git clone git@github.com:apache/hive.git
git checkout release-1.1.1
mvn clean package -Phadoop-2 eclipse:clean eclipse:eclipse -Pitests -DskipTests 
```
编译过程可能会失败, 一般是由于maven没有下载到jar包, 将命令重复执行几次就OK了.



为了清楚的理解Hive的SerDe, 首先自己开发一个简单的SerDe.

