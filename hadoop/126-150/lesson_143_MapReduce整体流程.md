从file.out.index文件中读取partition的信息。 然后将指定的partition读取到Reduce端， 如果partition比较小， 
则MergeManager.merge()返回`InMemoryMapOutput`文件。

整个Reduce任务的过程如下： 
1. ReduceTask.run()
--> new Shuffle()  
--> InMemoryMerger.start()   --- 合并
--> DiskMerger.start()       --- 合并
--> Shuffle.run()
----> LocalFetcher.start()   --- 取数据  

--------> scheduler.waitUntilDone() ------ 等待map任务全部完成， 才进行Sort操作。

--> MergeManager.close()   得到  RawKeyValueIterator   --- Sort操作

--> reduce()

--> TextOutputFormat.write()

Reduce一共5个步步骤。
Shuffle ---> Merge  --> Sort --> Reduce --> Write


Reduce端， 并不是等待所有的data从map端copy完了才进行merge操作的。 copy了一部分就会开始merge.
因此， 整个MapReduce做到了尽可能的并行化。

参考：
http://blog.csdn.net/aijiudu/article/details/72353510