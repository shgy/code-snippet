浮点数(Float/Double)在计算机中的存储存储遵循IEEE-754标准. 通常我们用到的是单精度(float)和双精度(double)这两种,对应的字节数是4byte和和8byte.
下面以Float为例, 来了解计算机是如何存储浮点数.
IEEE 754-1985 将存储空间分成三个部分，从最高位到最低位的顺序依次是：符号位(sign)、exponent(指数位)、fraction(分数位)。
其中sign占1-bit, exponent占8-bit, fraction占23-bit。 对于单精度: 1-8-23 (32)；对于双精度: 1-11-52 (64)  例如浮点数5.5，二进制表示如下:
```
------------------------------------------------
|   0 |1000 0001 |011 0000 0000 0000 0000 0000 |
------------------------------------------------
|Sign | exponent |        fraction             |
------------------------------------------------
```
上面这样的二进制数, 如何转换才能得到5.5呢?
```
v(5.5) = (-1)^s * 2^E * M 
```
首先处理符号位  s=0, 所以 (-1)^0 = 1 ； 

然后处理指数位. 指数位单独拎出来计算, 其值为
```python
>>> int('10000001',2)
129
```
2^E = 2^(129-127) = 4 ;
为什么要减去127呢?  这里的指数位采用的是biased exponent, 翻译过来就是`有偏移的指数`(本来应该是129, 无端减去127, 当然偏移了).
这样做使得负指数也表示成了一个正数，方便计算机对不同浮点数进行大小比较。不考虑符号, 8-bit的存储范围为[0,255], 中间值则是127,

最后处理分数位

23-bit fraction的处理与指数位不同, 我总结的8字秘诀就是`exponent看值, fraction数个.` 即对于23-bit fraction从左到右, 
第 1位 -- 2^(-1) = 0.5
第 2位 -- 2^(-2) = 0.25
       .
       .
第10位 -- 2^(-10) = 0.0009765625
       .
       .
第23位 -- 2^(-23)= 1.1920928955078125e-07
所以对于fraction `011 0000 0000 0000 0000 0000`
```
f = 1*2^(-2) + 1*2^(-3) = 0.375; 
M = f + 1 = 0.375
```
综上所述: `5.5 = 1 * 4 * 1.375`

对于fraction, 其值M的计算规则需要考虑exponent. 根据exponent的取值分为3种情况:  `e = 0 和 e =[1,254] 和 e=255`. 
由于Float的exponent只有8位, 所以其最大值为255.

e=[1,254] 是通常情况, 覆盖了99%以上的浮点数. 我们称之为`规格化的值`, 此时 `M= 1 + f`
e=0 是第一种特殊情况, 我们称之为`非规格化的值`, 此时 `M = f`
e=255是第二种特殊情况, 若fraction中23-bit全是0，表示无穷大(infinite); 否则表示NaN(Not a Number)

为了能够多看几个例子, 多做几个实验, 从而对这个转化过程形成感觉. 用python实现了两个简单的函数. 
一个是将浮点数转换成二进制字符串, 一个是将二进制字符串转换成浮点数.

```
def float2bin(num):
  return ''.join(bin(ord(c)).replace('0b', '').rjust(8, '0') for c in struct.pack('!f', num))


def bin2float(bits):
  return struct.unpack('f',struct.pack('I',int(bits,2)))

```

感谢stackoverflow贡献了如此精妙的实现方法.
```
>>> import struct
>>> def float2bin(num):
...   return ''.join(bin(ord(c)).replace('0b', '').rjust(8, '0') for c in struct.pack('!f', num))
... 
>>> 
>>> def bin2float(bits):
...   return struct.unpack('f',struct.pack('I',int(bits,2)))
... 
>>> float2bin(0.1)
'00111101110011001100110011001101'
>>> float2bin(1.0)
'00111111100000000000000000000000'
>>> float2bin(0.5)
'00111111000000000000000000000000'
>>> float2bin(2.0)
'01000000000000000000000000000000'
>>> 

```

当然, 也可以用Java查看一个Float的二进制字符串
```
System.out.println(Integer.toBinaryString(Float.floatToIntBits(5.5f)));
```
 
了解了Float的存储原理后, 再学习Lucene对Float的处理方法, 就简明很多了.

首先看一个简单的浮点数存储和检索的例子
```
package learn.learn;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRefBuilder;
import org.apache.lucene.util.NumericUtils;

public class NumericRangeQueryDemo {
	static Directory d = null;
	public static void index() throws IOException{
		d = FSDirectory.open(Paths.get("indexfile"));
		IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
		IndexWriter iw = new IndexWriter(d, conf);
		Document doc = new Document();
	    doc.add(new FloatField("f2", 2.5f, Field.Store.YES));
	    iw.addDocument(doc);
	    doc = new Document();
		iw.close();

	}
	
	public static void search() throws IOException{
		d = FSDirectory.open(Paths.get("indexfile"));
		IndexReader r = DirectoryReader.open(d);
		IndexSearcher searcher = new IndexSearcher(r);
		
		BytesRefBuilder act = new BytesRefBuilder();
		NumericUtils.intToPrefixCodedBytes(NumericUtils.floatToSortableInt(2.5f), 0, act);
		
		TopDocs n = searcher.search(new TermQuery(new Term("f2",act.get())), 2);
		System.out.println(n.totalHits);
		Document doc = searcher.doc(0);
		System.out.println(doc);
		
	}
	
	
	
	public static void floatToBytesRef(){
		System.out.println(Integer.toBinaryString(Float.floatToIntBits(5.5f)));
	}
	
	public static void main(String[] args) throws IOException {
//		index();
//		search();
//		floatToBytesRef();
	}
}

```

前面讲到Lucene处理Int类型是将int转换成6字节有序的字符串. 对于Float类型, 则是先转换成int, 然后按int类型的方式处理.
关键点在于`NumericUtils.floatToSortableInt()` . 题外话: 理解Lucene处理数值的原理,关键点在于理解`NumericUtils`类.

分析Float型数据, 与前面分析Int型数据一样, 正负拆开. 如果这个float是正数，那么把它看成int也是正数，而且根据前面的说明，指数在前，所以顺序也是保持好的。如果它是个负数，把它看成int也是负数，但是顺序就反了. 例如:
```
 float2bin(-1.0) = '10111111100000000000000000000000'
 float2bin(-2.0) = '11000000000000000000000000000000'
``` 
-1.0 > -2.0 但是, '10111111100000000000000000000000' < '11000000000000000000000000000000'
因此`NumericUtils.floatToSortableInt()`作了相应的处理
``` 
  // Lucene-5.2.0
  public static int sortableFloatBits(int bits) {
    return bits ^ (bits >> 31) & 0x7fffffff;
  }

```
根据运算符优先级, 计算顺序为`bits ^ ( (bits >> 31) & 0x7fffffff );` 注意这里的位移是`算术位移`, 即如果bits为负数，　则左移31位后，就变成了`0xffffffff`.

即 `符号位不变，　正数保持, 负数翻转`. 这样做虽然会导致 负数二进制字符串 > 正数二进制字符串 的情况出现, 但是`NumericUtils.intToPrefixCoded()`会做稍后的处理, 
所以最终保证了 Integer.compare(x,y) = String.compare(a,b)








参考:
http://blog.csdn.net/debiann/article/details/23012699
http://brokendreams.iteye.com/blog/2256239
http://blog.csdn.net/zhufenglonglove/article/details/51700898

