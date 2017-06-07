```
$ hdfs dfsadmin -fetchImage /tmp

$ ls -ltr /tmp | grep -i fsimage
-rw-r–r– 1 root root 22164 Aug 15 17:27 fsimage_0000000000000004389

$ hdfs oiv -i /tmp/fsimage_0000000000000001386 -o /tmp/fsimage.txt
```

参考:
file:///opt/hadoop-2.6.0/docs/r2.6.0/hadoop-project-dist/hadoop-hdfs/HdfsEditsViewer.html
file:///opt/hadoop-2.6.0/docs/r2.6.0/hadoop-project-dist/hadoop-hdfs/HdfsImageViewer.html

https://sranka.wordpress.com/tag/hdfs-dfsadmin-fetchimage/
http://lxw1234.com/archives/2015/08/440.htm
http://lxw1234.com/archives/2015/08/442.htm