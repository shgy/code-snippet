通过学习`org.apache.hadoop.hdfs.TestFileCreation`中的测试方法来学习Hdfs的基本操作.
```
conf.set("fs.file.impl", "org.apache.hadoop.fs.LocalFileSystem");
```
如果没有上面的配置项,则无法正确运行测试案例.
