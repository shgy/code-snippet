HDFS的配额分为两种: `Name Quotas`和`Space Quotas`.
```
cluster.getFileSystem().setQuota(new Path("/"), HdfsConstants.QUOTA_DONT_SET, 3 * 1024 * 1024);
```
一个方法同时可以设置`Name Quotas`和`Space Quotas`. 如果不设置, 则使用`HdfsConstants.QUOTA_DONT_SET`