在Lucene5.2中, 有一个类: `org.apache.lucene.util.packed.BulkOperation`, 该类中有一个方法
```java
  /**
   * For every number of bits per value, there is a minimum number of
   * blocks (b) / values (v) you need to write in order to reach the next block
   * boundary:
   *  - 16 bits per value -&gt; b=2, v=1
   *  - 24 bits per value -&gt; b=3, v=1
   *  - 50 bits per value -&gt; b=25, v=4
   *  - 63 bits per value -&gt; b=63, v=8
   *  - ...
   *
   * A bulk read consists in copying <code>iterations*v</code> values that are
   * contained in <code>iterations*b</code> blocks into a <code>long[]</code>
   * (higher values of <code>iterations</code> are likely to yield a better
   * throughput): this requires n * (b + 8v) bytes of memory.
   *
   * This method computes <code>iterations</code> as
   * <code>ramBudget / (b + 8v)</code> (since a long is 8 bytes).
   */
  public final int computeIterations(int valueCount, int ramBudget) {
    final int iterations = ramBudget / (byteBlockCount() + 8 * byteValueCount());
    if (iterations == 0) {
      // at least 1
      return 1;
    } else if ((iterations - 1) * byteValueCount() >= valueCount) {
      // don't allocate for more than the size of the reader
      return (int) Math.ceil((double) valueCount / byteValueCount());
    } else {
      return iterations;
    }
  }
```
有同学问我这个方法怎么理解? 看了几天, 感觉理解了一些, 就记录下来, 免得忘记了.
用于调试的代码如下:
```
// 参考 lucene/core/src/test中的org.apache.lucene.util.packed.testPackedInts()方法
public class BulkOperation_ {
	public static void main(String[] args) throws IOException {
		IOContext ctx  = new IOContext(new FlushInfo(100, 10));
		 final Directory d = new RAMDirectory();
	     IndexOutput out = d.createOutput("out.bin", ctx);
	     // 这里选择了bitsPerValue=50, 有助于理解整体逻辑
	     PackedInts.Writer w = PackedInts.getWriter(out, 68, 50, 0.0f); 
	     int[] a = {1,2,3,4,5};
	     for(int v :a){
	    	 w.add(v);
	     }
	     w.finish();
	}
}
/* pom.xml
	<dependency>
	    <groupId>org.apache.lucene</groupId>
	    <artifactId>lucene-core</artifactId>
	    <version>5.2.0</version>
	</dependency>
  </dependencies>
*/
```
### 个人理解 ###

需求:   对于有一定特征的数据, 如何能尽可能地以节约空间的方式存储? 

这里的特征指的是数据分布在一个区间中.

比如: 平台每天的访问量/应用每天的活跃人数/平台每天的订单数量. 这些数值一般都会有一个上限.

这就是为什么Lucene在使用Packed存储时, 者需要一个变量bitsPerValue, 即每个值需要的bit数量.

比如: 

      对于性别这个字段, bitsPerValue=1, 人妖就....

      对于省份这个字段, bitsPerValue=6, 中国的省份目前没有超过63个.

      ....
   
接下来就是理解computeIterations()方法注释中的这几个样例:
 ```
 *  - 16 bits per value -&gt; b=2, v=1
 *  - 24 bits per value -&gt; b=3, v=1
 *  - 50 bits per value -&gt; b=25, v=4
 *  - 63 bits per value -&gt; b=63, v=8
```
 ...
   首先需要明白的是变量b和变量v分别代表什么 ?

   b代表 byteBlockCount

   v代表 byteValueCount

   这两个变量在哪里呢? 通过调试代码发现在BulkOperationPacked.java文件中, 下面是它的构造方法
```
// org.apache.lucene.util.packed.BulkOperationPacked.java
  
  public BulkOperationPacked(int bitsPerValue) {
    this.bitsPerValue = bitsPerValue;
    assert bitsPerValue > 0 && bitsPerValue <= 64;
    int blocks = bitsPerValue;
    while ((blocks & 1) == 0) {
      blocks >>>= 1;
    }
    this.longBlockCount = blocks;
    this.longValueCount = 64 * longBlockCount / bitsPerValue;
    int byteBlockCount = 8 * longBlockCount;
    int byteValueCount = longValueCount;
    while ((byteBlockCount & 1) == 0 && (byteValueCount & 1) == 0) {
      byteBlockCount >>>= 1;
      byteValueCount >>>= 1;
    }
    this.byteBlockCount = byteBlockCount;   //  注意: 这就是上面的b
    this.byteValueCount = byteValueCount;   //  注意: 这就是上面的v
    if (bitsPerValue == 64) {
      this.mask = ~0L;
    } else {
      this.mask = (1L << bitsPerValue) - 1;
    }
    this.intMask = (int) mask;
    assert longValueCount * bitsPerValue == 64 * longBlockCount;
  }
```

尽管Lucene把数据的存储单位定位到了bit, 但是它太小了, 站在人的角度, 不够友好. 因此,在Lucene中, 通常使用byte[] 或者 long[] 来表示压缩过后的数据. 压缩后的数据, 以块为单位.即代码中反复出现的Block.

`16 bits per value -&gt; b=2, v=1   ` 是说 **每个块需要2个byte来存储, 这个块只能存储1个值**, 同理
`50 bits per value -&gt; b=25, v=4  ` 是说  **每个块需求50个byte来存储, 这个块只能存储 4个值**


对于16 bits per value的块, 它对应的代码是`new BulkOperationPacked16()`, 在BulkOperation第43行.
对于50 bits per value的块, 它对应的代码是`new BulkOperationPacked(50)`, 在BulkOperation第77行.

我理解这里花了很长一段时间.  上面的理解了, 就理解了computeIterations()方法一半的东西了. 接下来来理解下一半.即为什么 `iterations = ramBudget / (b + 8v)` ?


既然类的名字是BulkOperation, 即`批量处理`. 那么它需要一个缓冲池. 这个缓冲池定义多大比较好呢? 默认是1024 byte. 在Lucene中, 这个缓冲池的表现形式是这样的
```
//org.apache.lucene.util.packed.PackedWriter.java


final class PackedWriter extends PackedInts.Writer {

  boolean finished;
  final PackedInts.Format format;
  final BulkOperation encoder;
  final byte[] nextBlocks;   
  final long[] nextValues; 
  final int iterations;
  int off;
  int written;

  PackedWriter(PackedInts.Format format, DataOutput out, int valueCount, int bitsPerValue, int mem) {
    super(out, valueCount, bitsPerValue);
    this.format = format;
    encoder = BulkOperation.of(format, bitsPerValue);
    iterations = encoder.computeIterations(valueCount, mem);
    // 注意: 这两个数组就是Bulk操作的缓冲池
    nextBlocks = new byte[iterations * encoder.byteBlockCount()];  // 注意: 这是缓冲池的一部分
    nextValues = new long[iterations * encoder.byteValueCount()];  // 注意: 这是缓冲池的另一部分
    off = 0;
    written = 0;
    finished = false;
  }
```
其中, `nextBlocks`存储压缩后的数据, `nextValues`存储原始数据. 这两个数组加起来占用的空间不能超过ramBudget, 回顾一下这个公式`iterations = ramBudget / (b + 8v)`
对于每一个块(Block), 以bitsPerValue=50为例,  它用于存储压缩后数据的空间为b, 即25byte; 它用于存储原始数据的空间为8*v, 即8*4. 

为什么是8*v呢? 这个块一共可以存储４个数，每个数需要一个long来存储，　即8byte , 因此是8*v. 

综上:` iterations =  ramBudget / (b + 8v) `

在调试中, 取ramBudget=1024, 则 iterations=17 . 它代表着对于bitsPerValue=50的一批数据, 如果使用1024byte大小的内存作为缓冲池, 那么迭代17次即可以完成一个Bulk操作,
这个操作可以是压缩(encode), 也可以是还原(decode). 










