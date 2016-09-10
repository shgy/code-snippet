从上周开始就在做rootfs, 一直没弄好,今天竟然成功了.把过程记录一下.
参考: http://www.crifan.com/ubuntu_qemu_build_arm_rootfs/
```
$ tar -xf ~/Downloads/busybox-1.25.0.tar.bz2
$ cd busybox-1.25.0/
$ make defconfig
$ make menuconfig  # 编译成静态文件
$ make
$ make install
$ cd _install/
$ mkdir proc sys dev etc etc/init.d
$ gedit etc/init.d/rcS
$ cat etc/init.d/rcS 
#!/bin/sh
mount -t proc none /proc
mount -t sysfs none /sys
/sbin/mdev -s
$ chmod +x etc/init.d/rcS
$ find . | cpio -o --format=newc > ../../rootfs.img
qemu-system-x86_64 --kernel linux-2.6.24/arch/x86_64/boot/bzImage  --initrd rootfs.img --append "root=/dev/ram rdinit=/sbin/init"
```

操作系统是ubuntu 14.04 x86_64

之前使用busybox-1.16.0的版本, 结果一直无法编译成功.
