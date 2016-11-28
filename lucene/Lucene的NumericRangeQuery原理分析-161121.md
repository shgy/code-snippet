对于NumericRangeQuery的分析, `NumericUtils.splitRange()`是核心


 搜索的样例代码如下:
```

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class NumericRangeQueryDemo {
	static Directory d = null;
	public static void index() throws IOException{
		d = FSDirectory.open(Paths.get("indexfile"));
		IndexWriterConfig conf = new IndexWriterConfig(new StandardAnalyzer());
		IndexWriter iw = new IndexWriter(d, conf);
		Document doc =null;
		for(int i=0;i<512;i++)
		{
			doc = new Document();
			 doc.add(new IntField("f2", i, Field.Store.YES));
			 iw.addDocument(doc);
		}
	   
		iw.close();

	}
	
	public static void search() throws IOException{
		d = FSDirectory.open(Paths.get("indexfile"));
		IndexReader r = DirectoryReader.open(d);
		IndexSearcher searcher = new IndexSearcher(r);
		
		Query  query = NumericRangeQuery.newIntRange("f2", 0, 255, true, true);
		TopDocs n = searcher.search(query, 2);
		System.out.println(n.totalHits);
		Document doc = searcher.doc(0);
		System.out.println(doc);
		
	}
	
	public static void main(String[] args) throws IOException {
		index();
		search();
	}
}
```

我们先不管splitRange()代码的细节, 先根据前面理解到的知识, 来预测对于某一个[minBound,maxBound], `spiltRange`后在`NumericRangeQuery.NumericRangeTermsEnum.rangeBounds`中生成的结果是什么?

例如: 
```
当: precisitionStep=8, [minBound,maxBound]=[0, 16777215]时, rangeBounds=[[78 1 0], [78 1 0]]
当: precisitionStep=8, [minBound,maxBound]=[0, 65535]时, rangeBounds=[70 2 0 0], [70 2 0 0]
当: precisitionStep=8, [minBound,maxBound]=[0, 255]时, rangeBounds=[[68 4 0 0 0], [68 4 0 0 0]]
当: precisitionStep=8, [minBound,maxBound]=[0,1023]时, rangeBounds=[[68 4 0 0 0], [68 4 0 0 3]]
当: precisitionStep=8, [minBound,maxBound]=[0, 511]时, rangeBounds=[[68 4 0 0 0], [68 4 0 0 1]]
当: precisitionStep=8, [minBound,maxBound]=[0, 254]时, rangeBounds=[[60 8 0 0 0 0], [60 8 0 0 1 7e]]
当: precisitionStep=8, [minBound,maxBound]=[0, 127]时, rangeBounds=[[60 8 0 0 0 0], [60 8 0 0 0 7f]]
当: precisitionStep=8, [minBound,maxBound]=[10, 1023]时, rangeBounds=[[60 8 0 0 0 a], [60 8 0 0 1 7f], [68 4 0 0 1], [68 4 0 0 3]]
```
研究几个案例后, 关于splitRange()的逻辑, 就比较有感觉了. 例如: [minBound,maxBound]=[2, 1024]

首先会处理: [2,255], [1024,1024], 生成 [60 8 0 0 0 2], [60 8 0 0 1 7f], [60 8 0 0 8 0], [60 8 0 0 8 0]

然后会处理: [256,768], 生成 [68 4 0 0 1], [68 4 0 0 3]
所以最后splitRange生成的结果是`[[60 8 0 0 0 2], [60 8 0 0 1 7f], [60 8 0 0 8 0], [60 8 0 0 8 0],[68 4 0 0 1], [68 4 0 0 3]]`
结束.

总体的策略是先枝叶, 后主干.

通过上面的案例,结合前面理解的NumericTokenStream, 可以发现,在precisionStep=8时, [0,65535] 区间管理如下:
```
                 [0,65535]

[0,255], [256,511], ... , [62324,62579], [62580, 65535]

```

取值区间确定后, 当拿到的term比较多时, 一般是超过16个, 则使用bitset, 否则使用booleanQuery, 代码逻辑见`MultiTermQueryConstantScoreWrapper.createWeight()`. 在`MultiTermQueryConstantScoreWrapper.createWeight()`创建的`ConstantScoreWeight`对象的`rewrite()`方法.

最后, 再看具体代码的实现, 理解作者编码的细节, 每个变量的作用.
```
  /** This helper does the splitting for both 32 and 64 bit. */
  private static void splitRange(
    final Object builder, final int valSize,
    final int precisionStep, long minBound, long maxBound
  ) {
    if (precisionStep < 1)
      throw new IllegalArgumentException("precisionStep must be >=1");
    if (minBound > maxBound) return;
    for (int shift=0; ; shift += precisionStep) {
      // calculate new bounds for inner precision
      /*
       * diff的作用就是将每一轮的处理控制在算精度范围内, 以precisitionStep=8为例: 
       * diff=2^8
       * diff=2^16
       * diff=2^24
       * 即每一次扩大8-位
       * */
      final long diff = 1L << (shift+precisionStep),
        /*
         * mask, 直译就是掩码, 以precisionStep=8为例:
         * mask = 0x00000000000000ff
         * mask = 0x000000000000ff00
         * mask = 0x0000000000ff0000
         * */
        mask = ((1L<<precisionStep) - 1L) << shift;
      /*
       * hasLower/hasUpper 用于判别当前边界是枝叶还是树干. 主要作用于第一轮, 即shift=0时
       * */
      final boolean
        hasLower = (minBound & mask) != 0L,
        hasUpper = (maxBound & mask) != mask;
      /*
       * nextMinBound/nexMaxBound  可以形象理解为标记断点
       * */
      final long
        nextMinBound = (hasLower ? (minBound + diff) : minBound) & ~mask,
        nextMaxBound = (hasUpper ? (maxBound - diff) : maxBound) & ~mask;
      final boolean
        lowerWrapped = nextMinBound < minBound,
        upperWrapped = nextMaxBound > maxBound;
      /*
       * 这下面的逻辑就是真正的剪枝了, 需要注意的是, addRange会重新调整maxBound.
       * 例如: 对于区间[0,1024], 在这里看到的split后的区间是[0,768], [1024,1024],
       * 实际上,在addRange函数中,通过  maxBound |= (1L << shift) - 1L; 将区间修正为
       * [0,1023], [1024,1024]
       * */
      if (shift+precisionStep>=valSize || nextMinBound>nextMaxBound || lowerWrapped || upperWrapped) {
        // We are in the lowest precision or the next precision is not available.
        addRange(builder, valSize, minBound, maxBound, shift);
        // exit the split recursion loop
        break;
      }
      
      if (hasLower)
        addRange(builder, valSize, minBound, minBound | mask, shift);
      if (hasUpper)
        addRange(builder, valSize, maxBound & ~mask, maxBound, shift);
      
      // recurse to next precision
      minBound = nextMinBound;
      maxBound = nextMaxBound;
    }
  }
```


参考:
http://blog.csdn.net/zhufenglonglove/article/details/51700898


