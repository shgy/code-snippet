dfs.namenode.fs-limits.min-block-size

TestDatanodeDeath

在写数据的过程中, 如果有一个DataNode挂了, 不影响写数据的过程.

这就需要理解HDFS的恢复过程.

在往HDFS中写数据的过程中, 涉及到三处的数据恢复:
Lease recovery(租约恢复),
block recovery (数据块恢复),
pipeline recovery(管道链路恢复)

```
Before a client can write an HDFS file, it must obtain a lease, which is essentially a lock.
This ensures the single-writer semantics. The lease must be renewed within a predefined period of
time if the client wishes to keep writing. If a lease is not explicitly renewed or the client
holding it dies, then it will expire. When this happens, HDFS will close the file and release the
lease on behalf of the client so that other clients can write to the file.
This process is called lease recovery.
```

```
If the last block of the file being written is not propagated to all DataNodes in the pipeline,
then the amount of data written to different nodes may be different when lease recovery happens.
Before lease recovery causes the file to be closed, it’s necessary to ensure that all replicas of
the last block have the same length; this process is known as block recovery. Block recovery is only
triggered during the lease recovery process, and lease recovery only triggers block recovery on the
last block of a file if that block is not in COMPLETE state (defined in later section).
```

```
During write pipeline operations, some DataNodes in the pipeline may fail. When this happens, the underlying
write operations can’t just fail. Instead, HDFS will try to recover from the error to allow the pipeline to
keep going and the client to continue to write to the file. The mechanism to recover from the pipeline error
is called pipeline recovery.
```


问题1: 一个文件, 在只有一个DataNode的情况下, 能否设置多个副本?
问题2: 一个正在写的文件, 是可读的, 那么数据一致性如何保证?
问题3: NameNode删除文件的过程是怎样的?
问题4: 对于挂掉的DataNode, NodeNode如何处理?  pipeline上的DataNode如何处理?



参考:
http://blog.cloudera.com/blog/2015/02/understanding-hdfs-recovery-processes-part-1/
http://www.tuicool.com/articles/vaqeQne

