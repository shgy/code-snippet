学习HDFS的过程如下:
1. 概览HDFS的各个测试样例,  目的, 建立对HDFS各种功能, 操作方式的基本映像, 有基本感觉即可. 能懂就懂, 不能懂就略过.
   相当了解各基础类的正确使用方法(TestCase中包含了各个类的使用方式.)  混个脸熟. 

2. 比较细致了解HDFS的各个测试样例. 由于第一次看, 不了解各个知识点的脉络和关系,很多知识点必然是遗落的, 不理解的或者理解错误的.
   需要回头再拣起来, 建立整体的脉络.  摸清有哪些门路, ,几个山头.

3. 深入研读HDFS的各个测试样例. 第二次看, 是为了建立大局观, 整体的感觉. 因此很多细节就略过了, 对于重点和非重点也不甚明了. 
   这次需要厘清重点, 理解细节. 同时, 对每个操作的基本过程要了然于胸.  拜码头.

4. 第四次学习HDFS的测试样例. 了解背后的原理与限制. 

testExcludedNodesForgiveness:  如果一个DataNode掉线了, 但随后,即在2.5s内又回来了, 则相当于该DataNode没有掉线. 即原谅(Forgive)掉线的DataNode

testMkdirRpcNonCanonicalPath: 如果NameNode中文件名字不合法, `Creating file with invalid path can corrupt edit log` 比如` hdfs://localhost:8020//path/file`

