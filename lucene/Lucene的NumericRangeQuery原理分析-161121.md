对于NumericRangeQuery的分析, `NumericUtils.splitRange()`是核心

首先来理解`splitRange()`.

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
当: precisitionStep=8, [minBoun,maxBound]=[0, 16777215]时, rangeBounds=[[78 1 0], [78 1 0]]
当: precisitionStep=8, [minBoun,maxBound]=[0, 65535]时, rangeBounds=[70 2 0 0], [70 2 0 0]
当: precisitionStep=8, [minBoun,maxBound]=[0, 255]时, rangeBounds=[[68 4 0 0 0], [68 4 0 0 0]]
当: precisitionStep=8, [minBoun,maxBound]=[0,1023]时, rangeBounds=[[68 4 0 0 0], [68 4 0 0 3]]
当: precisitionStep=8, [minBoun,maxBound]=[0, 511]时, rangeBounds=[[68 4 0 0 0], [68 4 0 0 1]]
当: precisitionStep=8, [minBoun,maxBound]=[0, 254]时, rangeBounds=[[60 8 0 0 0 0], [60 8 0 0 1 7e]]
当: precisitionStep=8, [minBoun,maxBound]=[0, 127]时, rangeBounds=[[60 8 0 0 0 0], [60 8 0 0 0 7f]]
当: precisitionStep=8, [minBoun,maxBound]=[10, 1023]时, rangeBounds=[[60 8 0 0 0 a], [60 8 0 0 1 7f], [68 4 0 0 1], [68 4 0 0 3]]

研究几个案例后, 关于splitRange()的逻辑, 就比较有感觉了. 例如: [minBoun,maxBound]=[2, 1024]
首先会处理: [2,255], [1024,1024]
然后会处理: [256,768]
结束.
总体的策略是先枝叶, 后主干.
[0,65535] 区间管理如下:
```
                 [0,65535]

[0,255], [256,511], ... , [62324,62579], [62580, 65535]

```

取值区间确定后, 当拿到的term比较多时, 一般是超过16个, 则使用bitset, 否则使用booleanQuery, 代码逻辑见`MultiTermQueryConstantScoreWrapper.createWeight()`. 在`MultiTermQueryConstantScoreWrapper.createWeight()`创建的`ConstantScoreWeight`对象的`rewrite()`方法.


