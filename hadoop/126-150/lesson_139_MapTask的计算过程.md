对于Map Task, 它会将处理的结果暂时放到一个缓冲区中， 当缓冲区使用率到达一定阈值后，
再对缓冲区中的数据进行一次排序， 并将这些有序数据以IFile文件形式写到磁盘上， 而当数据处理完毕后， 它
会对磁盘上所有文件进行一次合并， 以将这些文件合并成一个大的有序文件。

map阶段：
kvbuffer ---->  spill.out (spill)  ---->  file.out (combine)

由于数据存放在内存缓冲区中， 缓冲区满了才写入到硬盘， 所以称为spill。

如果maptask处理的数据量分成的spill.out文件小于3个（mapreduce.map.combine.minspills），并且配置了combine，
那么在map端就会做一些合并操作。