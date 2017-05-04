<hadoop in action>中关于combiner的解释, 非常好.
以wordcount为例, 在map阶段, 如果一篇文章中有574个"the", 会生成574个<the,1>的key-value
在传向reduce时, 如果在map端进行合并(combiner)操作, 会得到一个<the,574> ,这会极大节约网络传输的流量.
由于combiner与reducer的逻辑是一样的, 所以combiner又可以理解为map端的reduce操作.