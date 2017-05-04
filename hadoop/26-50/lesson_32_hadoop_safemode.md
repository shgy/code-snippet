就像操作系统启动的时候会先进入实模式, 做一些准备的工作；  Hadoop文件系统启动时也会进入一个安全模式. 所谓安全模式, 也可以称为只读模式.

安全模式主要是为了系统启动的时候检查各个DataNode上数据块的有效性, 同时根据策略必要的复制或者删除部分数据块。运行期通过命令也可以进入安全模式。

```
格式：Usage: java DFSAdmin [-safemode enter | leave | get |wait]
用户可以通过dfsadmin -safemode value 来操作安全模式，参数value的说明如下：
enter - 进入安全模式
leave - 强制NameNode离开安全模式
get   - 返回安全模式是否开启的信息
wait  - 等待，一直到安全模式结束。
```

相关的源代码在FSNameSystem.SafeModeInfo数据结构中
