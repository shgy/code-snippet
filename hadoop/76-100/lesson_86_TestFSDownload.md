Container的Localizing过程， 很重要的一点就是将执行Container命令需要的数据下载到Container的工作目录， 这一任务由FsDownload负责。

1. testDownloadBadPublic()
   这里设置了umask
   ```
       conf.set(CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY, "077");
   ```
   即所有创建的文件，目录都没有用户和组的所有权限。
   也就不能正常下载。
   所以注释该行代码， 使用默认的umask (0022) 即可成功下载文件了。

   真正下载文件的是： FileUtil.copy()方法。