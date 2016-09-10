使用的操作系统是Ubuntu-14.04 x86_64. 编译的内核版本是linux-2.6.24, 由于以<独辟蹊径品内核: Linux内核源码导读>为参考书目. 
参考: 
http://www.sw-at.com/blog/2011/02/11/linux-kernel-development-and-debugging-using-eclipse-cdt/
http://www.cnblogs.com/syw-casualet/p/5271369.html
http://stackoverflow.com/questions/23194840/linux-2-6-24-kernel-compilation-error-size-expression-for-copy-user-generic-c-d

编译过程:
```
cd ~/linux-2.6.24
yes "" | make oldconfig
make menuconfig O=~/linux-2.6.24-obj
make O=~/linux-2.6.24-obj
```
中间出了一点错误, 记录如下:

1. 修改arch/x86/vsod/Makefile中的`-m elf_x86_64`为 `-m64`即可.
1. 出错信息
```
  LDS arch/x86/kernel/vsyscall_32.lds
  AS arch/x86/kernel/vsyscall-int80_32.o
  AS arch/x86/kernel/vsyscall-note_32.o
  SYSCALL arch/x86/kernel/vsyscall-int80_32.so
gcc: error: elf_i386: No such file or directory
```
解决方法:
```
61c61
< cmd_syscall = $(CC) -m elf_i386 -nostdlib $(SYSCFLAGS_$(@F)) \
---
> cmd_syscall = $(CC) -m32 -nostdlib $(SYSCFLAGS_$(@F)) \
```
2. 出错信息
```
LD .tmp_vmlinux1
kernel/built-in.o: In function `mutex_lock':
/work/x86/2.6.24/linux-2.6.24/kernel/mutex.c:92: undefined reference to `__mutex_lock_slowpath'
kernel/built-in.o: In function `mutex_unlock':
/work/x86/2.6.24/linux-2.6.24/kernel/mutex.c:118: undefined reference to `__mutex_unlock_slowpath'
make: *** [.tmp_vmlinux1] Error 1
```
解决方法:
```
sun@ubuntu:/work/x86/2.6.24/linux-2.6.24$ vi kernel/mutex.c
61c61
< static void fastcall noinline __sched
---
> static __used void fastcall noinline __sched
98c98
< static void fastcall noinline __sched
---
> static __used void fastcall noinline __sched
263c263
< static fastcall noinline void
---
> static __used fastcall noinline void
```
3. 出错信息
```
.size expression for copy_user_generic_c does not evaluate to a constant
```
解决方法
```
you should find arch/x86/lib/copy_user_64.S ,change the END(copy_user_generic_c) into END(copy_user_generic_string) in the file,to keep the same variable in the ENTRY(copy_user_generic_string).
```


制作rootfs文件

网上使用busybox的方法制作rootfs我没有走通, 使用的是下面的方法. 但是下面的方法有一个问题, 无法使用任何命令. 估计还是应该好好试验如何使用busybox制作rootfs.
```
git clone https://github.com/mengning/menu.git(这个是上课老师提供的材料)
cd menu
gcc -o init linktable.c menu.c test.c -m32 -static –lpthread
(在这一步的时候，出现了类似 fatal error: sys/cdefs.h: No such file or directory 的错误提示，搜索以后发现在ubuntu amd64下，需要下载一个包，下载的命令是：apt-get install libc6-dev-i386, 安装gcc-multilib 和g++-multilib 也是可以的<br>具体参考http://askubuntu.com/questions/470796/fatal-error-sys-cdefs-h-no-such-file-or-directory)
cd ../rootfs
cp ../menu/init ./
find . | cpio -o -H newc |gzip -9 > ../rootfs.img
```

然后使用命令启动虚拟机
```
qemu-system-x86_64 --kernel linux-2.6.24/arch/x86_64/boot/bzImage  --initrd rootfs.img -s -S
```
在使用eclipse进行debug前, 最好首先在命令中使用gdb测试一下. 我是这么做的. 关于gdb, 在64位的操作系统中,可能出现如下的问题
```
  # http://blog.sina.com.cn/s/blog_858820890101a66p.html
  Remote 'g' packet reply is too long: 00000000000000000020e30100000000000000000000
  000060eecf81ffffffff000000010000000048eecf81fffff
```
使用网上的方法,修改gdb的源码,然后编译安装即可.

然后下载C/C++版的eclipse
```
Eclipse IDE for C/C++ Developers

Version: Kepler Service Release 2
Build id: 20140224-0627

(c) Copyright Eclipse contributors and others 2000, 2014.  All rights reserved.
Visit http://eclipse.org/
```
按照网上的相关教程添加相关的插件. 
http://x-slam.com/da_jian_eclipse_qemu_gdb_diao_shi_linux_kernel_huan_jing


关键在于选择GDB时,一定要选择"GDB(DSF) Manual Remote Debuging Launcher", 将端口设置为1234.


