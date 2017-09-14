MapTask从InputSplit中读取数据， 进行Mapper处理。Mapper处理的结果存储于环形缓冲区中， 缓冲区满了就刷到硬盘中，形成临时文件。
Mapper完成后就将临时文件合并，最后形成一个大文件。
