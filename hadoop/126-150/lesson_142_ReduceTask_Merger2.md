所谓巧妇难为无米之炊
MapTask的“米”是InputSplit， 即用户准备好的数据。 ReduceTask的“米”则是MapTask加工后处理好的数据。
这里有如下的问题需要解决：
1. MapTask的输出在MapTask的工作目录，也就是local机器上， 并不在hdfs上面。 
   ReduceTask需要从各个MapTask取自己需要处理的那个partition.

2. ReduceTask从各个Map获取的数据，是局部有序， 需要经过Merger, 变成整体有序。才能让ReduceTask正确的处理。

得找一个Merger使用的简单demo， 了解Merger是咋个用的。
``` 
package wordcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.Progress;
import org.apache.hadoop.util.Progressable;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by shgy on 17-10-2.
 */
public class MockReader {

    private static Answer<?> getKeyAnswer(final String segmentName,
                                   final boolean isCompressedInput) {
        return new Answer<Object>() {
            int i = 0;

            @SuppressWarnings("unchecked")
            public Boolean answer(InvocationOnMock invocation) {
                if (i++ == 3) {
                    return false;
                }
                IFile.Reader<Text,Text> mock = (IFile.Reader<Text,Text>) invocation.getMock();
                int multiplier = isCompressedInput ? 100 : 1;
                mock.bytesRead += 10 * multiplier;
                Object[] args = invocation.getArguments();
                DataInputBuffer key = (DataInputBuffer) args[0];
                key.reset(("Segment Key " + segmentName + i).getBytes(), 20);
                return true;
            }
        };
    }

    private static Answer<?> getValueAnswer(final String segmentName) {
        return new Answer<Void>() {
            int i = 0;

            public Void answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                DataInputBuffer key = (DataInputBuffer) args[0];
                key.reset(("Segment Value " + segmentName + i).getBytes(), 20);
                return null;
            }
        };
    }
    private static IFile.Reader<Text, Text> getReader(int i, boolean isCompressedInput)
            throws IOException {
        IFile.Reader<Text, Text> readerMock = mock(IFile.Reader.class);
//        when(readerMock.getLength()).thenReturn(30l);
//        when(readerMock.getPosition()).thenReturn(0l).thenReturn(10l).thenReturn(
//                20l);
        when(
                readerMock.nextRawKey(any(DataInputBuffer.class)))
                .thenAnswer(getKeyAnswer("Segment" + i, isCompressedInput));
        doAnswer(getValueAnswer("Segment" + i)).when(readerMock).nextRawValue(
                any(DataInputBuffer.class));

        return readerMock;
    }

    private static Merger.Segment<Text, Text> getUncompressedSegment(int i) throws IOException {
        return new Merger.Segment<Text, Text>(getReader(i, false), false);
    }
    private static Progressable getReporter() {
        Progressable reporter = new Progressable() {
            //      @Override
            public void progress() {
            }
        };
        return reporter;
    }

    public static void main(String[] args) throws IOException {

        List<Merger.Segment<Text, Text>> segments = new ArrayList<Merger.Segment<Text, Text>>();
        for (int i = 0; i < 2; i++) {
            segments.add(getUncompressedSegment(i));
        }

        Configuration conf = new Configuration();
        JobConf jobConf = new JobConf();
        FileSystem fs = FileSystem.getLocal(conf);

        Path tmpDir = new Path("localpath");
        Class<Text> keyClass = (Class<Text>) jobConf.getMapOutputKeyClass();
        Class<Text> valueClass = (Class<Text>) jobConf.getMapOutputValueClass();
        RawComparator<Text> comparator = jobConf.getOutputKeyComparator();
        Counters.Counter readsCounter = new Counters.Counter();
        Counters.Counter writesCounter = new Counters.Counter();
        Progress mergePhase = new Progress();
        RawKeyValueIterator mergeQueue = Merger.merge(conf, fs, keyClass,
                valueClass, segments, 2, tmpDir, comparator, getReporter(),
                readsCounter, writesCounter, mergePhase);

        while(mergeQueue.next()){
            DataInputBuffer keyBuff = mergeQueue.getKey();
            DataInputBuffer valBuff = mergeQueue.getValue();

            Text key = new Text(keyBuff.getData());
            Text val = new Text(valBuff.getData());
            System.out.println(key.toString()+" : "+ val.toString());

        }

    }
}

```

前面已经说了， Merger的作用是让局部有序的数据块整体有序输出。 如何满足这个需求呢？

可以看出这本质上是一个排序的问题。 hadoop做法是选择堆排序算法。 为啥用堆排序呢？
(个人推测)
1. Segment可以在内存中， 也可以在硬盘上。 如果预先排好序再处理， 免不了多添加一个流水线，增加复杂度。
2. 堆排序， 从某种程度上来说， 可视为懒操作。 能够避免一些计算资源和内存的浪费。




