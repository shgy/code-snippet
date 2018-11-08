ES的官方文档中关于 检索和排序的关系说得特别好：
```
Search needs to answer the question "Which documents contain this term?", while sorting and aggregations need to answer a different question: "What is the value of this field for this document?".
```

搜索要解决的问题是: "哪些文档包含给定的关键词？"
排序和聚合要解决的问题是： “这个文档的字段的值是多少？”


同样，以需求为出发点: "检索的结果按时间排序" 这个需求在商品搜索和日志分析系统中是非常普遍的。 众所周知，Lucene是通过倒排索引解决了“检索的问题”，那么“排序的问题” 怎么处理呢？


最开始，Lucene是通过FieldCache来解决这个需求。就是通过FieldCache建立`docId - value`的映射关系。 但是FieldCache有个两个致命的问题: 堆内存消耗  和 首次加载耗时 。 如过索引更新频率较高，这两个问题引发的GC和超时导致系统不稳定估计是程序员的噩梦。

从Lucene4.0开始，引入了新的组件IndexDocValues，就是我们常说的`doc_value`。

它有两个亮点： 
```
1. 索引数据时构建 doc-value的映射关系。注: 倒排索引构建的是value-doc的映射关系。

2. 列式存储
```

这基本上就是“空间换时间”和“按需加载”的典型实践了。 而且，列式存储基本上是所有高效NoSQL的标配，Hbase, Hive 都有劣势存储的身影。


IndexDocValues跟FieldCache一样解决了“通过`doc_id`查询value”的问题， 同时也解决了FieldCache的两个问题。 


ES基于`doc_value`构建了`fielddata`, 用于排序聚合。 所以，可以毫不客气地说， `doc_value`是ES `aggregations`的基石。

那么在ES中， `fielddata`如何使用呢？ 以binary类型为例，参考: `org.elasticsearch.index.fielddata.BinaryDVFieldDataTests`


s1: 建mappings时需要特殊处理
```
        String mapping = XContentFactory.jsonBuilder().startObject().startObject("test")
                .startObject("properties")
                .startObject("field")
                .field("type", "binary")
                .startObject("fielddata").field("format", "doc_values").endObject()
                .endObject()
                .endObject()
                .endObject().endObject().string();
```

s2: 通过leafreader构建`doc_values`

```
 LeafReaderContext reader = refreshReader();
        IndexFieldData<?> indexFieldData = getForField("field");
        AtomicFieldData fieldData = indexFieldData.load(reader);

        SortedBinaryDocValues bytesValues = fieldData.getBytesValues();
```

s3: 定位到指定文档, 用`setDocument()`方法。
```
/**
 * A list of per-document binary values, sorted
 * according to {@link BytesRef#getUTF8SortedAsUnicodeComparator()}.
 * There might be dups however.
 */
public abstract class SortedBinaryDocValues {

    /**
     * Positions to the specified document
     */
    public abstract void setDocument(int docId);

    /**
     * Return the number of values of the current document.
     */
    public abstract int count();

    /**
     * Retrieve the value for the current document at the specified index.
     * An index ranges from {@code 0} to {@code count()-1}.
     * Note that the returned {@link BytesRef} might be reused across invocations.
     */
    public abstract BytesRef valueAt(int index);

}
```
注意，如果reader是组合的，也就是有多个，需要用到`docBase + reader.docId`。 这里是容易采坑的。

s4: 获取文档的指定field的value,使用 `valueAt()`方法。

最后总结一下， 本文简述了lucene的`doc_value`和 es的`fielddata`的关系， 简要描述了一下`doc_value`的基本思想。最后给出了在ES中使用fielddata的基本方法。这对于自己开发plugin是比较有用的。 




