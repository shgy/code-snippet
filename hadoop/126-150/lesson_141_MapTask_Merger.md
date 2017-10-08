详情参见 `org.apache.hadoop.mapred.MapTask.MapOutouptBuffer.mergeParts()`

map()函数执行的结果， 写入到 circular buffer. 然后写入到spill.out文件， 通常情况下， 每个spill.out文件50M左右。
待map()函数执行完毕， 也就是当前的InputSplit处理完成后, 需要将spill.out文件合并， 生成file.out文件， 方便reduce程序的处理。


1. 通过SpillThread可知， 每个spill.out文件都是有序的， 其格式为IFile. 

2. file.out是整体有序的。  单独提取出一个file.out文件， 写代码来读取一下。

```
package maptask;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.IFile;

import java.io.IOException;

/**
 * Created by shgy on 17-9-30.
 */
public class FileOutReader {
    public static void main(String[] args) throws IOException {

        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
        FileSystem fs = FileSystem.get(conf);
        IFile.Reader<Text,IntWritable> reader = new IFile.Reader<Text,IntWritable>(
                conf,fs, new Path("/home/shgy/tmp/maptask/file.out"),null,null
        );


        DataInputBuffer keyBuff = new DataInputBuffer();
        DataInputBuffer valueBuff = new DataInputBuffer();
        Text key = new Text();
        IntWritable value = new IntWritable();
        while (reader.nextRawKey(keyBuff)) {
            key.readFields(keyBuff);
            reader.nextRawValue(valueBuff);
            value.readFields(valueBuff);
            System.out.println(key.toString() + ": "+ value.get());
        }

        reader.close();
    }
}
```

生成file.out以后， maptask的任务就完成了。 


Merger是一个工具类， 在Map和Reduce中都有使用。

Merger类在merge的过程中， 会生成中间文件intermediate。 

Segment是对磁盘和内存中的IFile格式文件的抽象。 它具有类似与迭代器的功能， 可迭代读取IFile文件中的key/value

Merger采用了多轮递归合并的方式。