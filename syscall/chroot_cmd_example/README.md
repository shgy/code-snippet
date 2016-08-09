学习chroot命令,参考: http://www.ibm.com/developerworks/cn/linux/l-cn-chroot/
```
$ pwd
/home/shgy/github-public/code-snippet/syscall/chroot_cmd_example
$ wget https://busybox.net/downloads/binaries/busybox-x86_64
$ mv busybox-x86_64 busybox && chmod u+x busybox
$ ./busybox pwd
/home/shgy/github-public/code-snippet/syscall/chroot_cmd_example
$ mkdir bin
$ ln -s ../busybox bin/ash
$ ln -s ../busybox bin/ls
$ cat bin/bash
#!/bin/ash
ash
$ sudo chroot .
/ # pwd
/
/ # 

```
 
###这里有坑###
创建bin/ash的软链接时,一定要使用命令:`ln -s ../busybox bin/ash`  通过软链接的位置定位源文件.



