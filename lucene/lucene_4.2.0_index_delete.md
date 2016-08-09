###Lucene4.2.0中索引的删除###

以前看Lucene 源码的时候, 只了解了Lucene添加索引部分的实现. 并没有深入理解Lucene是如何删除数据的.

在合并索引数据之前, Lucene把删除的数据存储在.del为后缀的文件中. del文件仅对其对应的段负责.
我们都说Lucene的segment落盘后就不可修改了,
确实如此. 但是与segment相关的del文件是可能会变化的.

参考lucene的官方文档:
https://lucene.apache.org/core/4_2_0/core/org/apache/lucene/codecs/lucene40/Lucene40LiveDocsFormat.html

其格式非常容易理解:
 Format,Header,ByteCount,BitCount, Bits | DGaps (depending on Format)
我们知道, 段中的每一个文档只需要一个bit位在确定它是否被删除.例如`00001000`表示docid=3的文档标记为删除状态.
由于Lucene适用的场景就是一次写入,多次读取. 那么删除一般量级是比较小的.官方文档给出了一个例子:
```
For example, if there are 8000 bits and only bits 10,12,32 are cleared, DGaps would be used:

(VInt) 1 , (byte) 20 , (VInt) 3 , (Byte) 1
```
这就引入了DGaps这个概念. 如果只使用Bits来存储, 那么将是非常浪费空间的.Bits存储的状态参考lucene_de.jpg图片.
需要注意的是Lucene在存储del文件使用了"差值规则". 
本来应该是`(VInt) 1 , (byte) 20 , (VInt) 4 , (Byte) 1`, 这里变成了 `(VInt) 1 , (byte) 20 , (VInt) 3 (4-1) , (Byte) 1`
参考了http://forfuture1978.iteye.com/blog/546841
