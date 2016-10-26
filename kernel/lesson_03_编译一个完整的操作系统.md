参考<深度探索Linux操作系统: 系统构建和原理解析>

 奋战几天,将结果记录一下, 终于还是将本书前3章所描述的内容实践出来了. 

最开始使用的是ubuntu-14.04-X86_64的系统, 结果编译glibc的时候卡住了, 没有stubs-32.h头文件.
换成了书中描述的32位系统,由于11.10版本实在是比较老了, 因此使用的是ubuntu-14.04-server-i386. 

然后就是在系统上配置账户体系, 准备基础环境.
```
groupadd vita
useradd -m -s /bin/bash -g vita vita

mkdir source build cross-tool cross-gcc-tmp sysroot
chown -R vita.vita /vita
vim /home/vita/.bashrc
unset LANG
export HOST=i686-pc-linux-gnu
export BUILD=$HOST
export TARGET=i686-none-linux-gnu
export CROSS_TOOL=/vita/cross-tool
export CROSS_GCC_TMP=/vita/cross-gcc-tmp
export SYSROOT=/vita/sysroot
PATH=$CROSS_TOOL/bin:$CROSS_GCC_TMP/bin:/sbin:/usr/sbin:$PATH
```
在设置PATH时, 我使用的是`PATH=$CROSS_TOOL/bin:/sbin:/usr/sbin:$PATH`, 这是因为在编译glibc的时候, 会引用到stdio.h头文件, 而`$SYSROOT`中并没有准备好stdio.h头文件.

接下来就是下载需要的源码包
```
# 所有的源码包都放在/vita/source目录下
su - vita && cd /vita/source
wget http://ftp.gnu.org/gnu/binutils/binutils-2.23.1.tar.bz2
wget http://mirrors.ustc.edu.cn/gnu/gcc/gcc-4.7.3/gcc-4.7.3.tar.bz2
wget https://gmplib.org/download/gmp/gmp-5.0.5.tar.bz2
wget http://ftp.gnu.org/gnu/mpfr/mpfr-3.1.1.tar.bz2
wget http://ftp.heanet.ie/mirrors/gnu/mpc/mpc-1.0.1.tar.gz
wget http://ftp.gnu.org/gnu/glibc/glibc-2.15.tar.bz2
wget http://ftp.gnu.org/gnu/glibc/glibc-ports-2.15.tar.bz2
wget https://www.kernel.org/pub/linux/kernel/v3.x/linux-3.7.4.tar.xz
wget http://ftp.gnu.org/gnu/bash/bash-4.2.tar.gz
```
接下来就是按步骤编译,为了在编译的时候不用每次输入冗长的命令,我将这些命令全部脚本化了.
将脚本放在/vita/build目录下即可.
```
tree lesson_03
lesson_03
├── clean-dir.sh
├── ldd.sh
├── s1-build-binutils.sh
├── s2-build-gcc-s1.sh
├── s2-build-gcc-s2.sh
├── s3-install-header.sh
├── s3-link-libgcc.sh
├── s4-build-glibc-s1.sh
├── s4-build-glibc-s2.sh
├── s5-build-gcc-s3.sh
└── s6-build-bash.sh

```
编译第一步: binutils的编译最顺利了, 没有什么值得说的.
```
cd binutils-build
../binutils-2.23.1/configure \
--prefix=$CROSS_TOOL --target=$TARGET \
--with-sysroot=$SYSROOT
make
make install
```
编译第二步: 书中的gcc版本是4.7.2, 我编译的时候编译不出来,具体的错误是什么忘记了, 就换成4.7.3了.
```
# 解压源码
tar xvf ../source/gcc-4.7.3.tar.bz2
cd gcc-4.7.3
tar xvf ../../source/gmp-5.0.5.tar.bz2
mv gmp-5.0.5/ gmp
tar xvf ../../source/mpfr-3.1.1.tar.bz2
mv mpfr-3.1.1/ mpfr
tar xvf ../../source/mpc-1.0.1.tar.gz
mv mpc-1.0.1/ mpc
mkdir ../gcc-build 
```
```
# 编译
cd gcc-build
../gcc-4.7.3/configure \
--prefix=$CROSS_GCC_TMP --target=$TARGET \
--with-sysroot=$SYSROOT \
--with-newlib --enable-languages=c \
--with-mpfr-include=/vita/build/gcc-4.7.3/mpfr/src \
--with-mpfr-lib=/vita/build/gcc-build/mpfr/src/.libs \
--disable-shared --disable-threads \
--disable-decimal-float --disable-libquadmath \
--disable-libmudflap --disable-libgomp \
--disable-nls --disable-libssp

make
make install
```
我最开始把`--disable-libquadmath` 写成了`--disable-libquadmatch`结果一直报错.

编译第三步:然后就是链接和安装kernel_headers了, 书中估计是排版的原因, 有些空格没有出来
```
cd /vita/cross-gcc-tmp/
ln -s libgcc.a lib/gcc/i686-none-linux-gnu/4.7.3/libgcc_eh.a
cd linux-3.7.4
make ARCH=i386 INSTALL_HDR_PATH=$SYSROOT/usr/ headers_install
```
编译第四步：glibc的编译也比较坑, 没有找到书上说的patch, 使用了ports
```
tar xvf ../source/glibc-2.15.tar.xz
tar xvf ../source/glibc-ports-2.15.tar.bz2
mv glibc-ports-2.15.tar.gz glibc-2.15/ports
mkdir glibc-build
```
与书上不一致的地方在于 `CC="gcc -U__i686"`和`make CFLAGS="-U_FORTIFY_SOURCE -O2 -fno-stack-protector"`
```
cd glibc-build
../glibc-2.15/configure \
--prefix=/usr  CC="gcc -U__i686" --host=$TARGET \
--enable-kernel=3.7.4 --enable-add-ons \
--with-headers=$SYSROOT/usr/include \
--without-selinux \
libc_cv_forced_unwind=yes libc_cv_c_cleanup=yes \
libc_cv_ctors_header=yes
make CFLAGS="-U_FORTIFY_SOURCE -O2 -fno-stack-protector"
make install_root=$SYSROOT install
```
编译第五步:
```
cd gcc-build
../gcc-4.7.3/configure \
--prefix=$CROSS-TOOL --target=$TARGET \
--with-sysroot=$SYSROOT \
--enable-languages=c,c++ \
--with-mpfr-include=/vita/build/gcc-4.7.3/mpfr/src \
--with-mpfr-lib=/vita/build/gcc-build/mpfr/src/.libs \
--enable-threads=posix
make && make install
```
然后就是按照书上所描述的, 设置.bashrc
```
# stage two
export CC="$TARGET-gcc"
export CXX="$TARGET-g++"
export AR="$TARGET-ar"
export AS="$TARGET-as"
export RANLIB="$TARGET-ranlib"
export LD="$TARGET-ld"
export STRIP="$TARGET-strip"

export DESTDIR=$SYSROOT
```
像pkg-config和libtool这块的知识我是忽略过去了.

编译第六步: 编译内核, 这一步进行得很顺利,略过.

接下来就是在virtual-box中准备虚拟机, 分区的时候一定要留一个已经分好区, 但是没有挂载的空间.
由于我在虚拟机中使用的是`ubuntu-14.04-server-i386`系统, 设置grub的启动项与书上描述有些出入
```
$ cat /etc/grub.d/40_custom 
#!/bin/sh
echo "add custom40."  >&2
exec tail -n +4 $0
# This file provides an easy way to add custom menu entries.  Simply type the
# menu entries you want to add after this comment.  Be careful not to change
# the 'exec tail' line above.

menuentry "vita" {
   set root="(hd0,msdos3)"
   linux /boot/bzImage root=/dev/sda3 ro init=/bin/bash

}
$ update-grub
Generating grub configuration file ...
Found linux image: /boot/vmlinuz-4.2.0-27-generic
Found initrd image: /boot/initrd.img-4.2.0-27-generic
Found memtest86+ image: /boot/memtest86+.elf
Found memtest86+ image: /boot/memtest86+.bin
Found unknown Linux distribution on /dev/sda3
add custom40.
done
```
一定不能忘记的是执行`update-grub`命令.

关于`set root="(hd0,msdos3)"`而非书中描述的`set root="(hd0,1)"`, 这里我是这样确定的:
在启动到grub的菜单项时, 按`c`进入grub的命令行.(见lesson_03中的截图)
然后使用`ls (hd0,msdos3)/`命令来确定目标分区.

编译第七步: 编译bash, 这一步有一点小波折. 使用动态链接库出现了`kernel panic`. 我使用了`--enable-static-link`,脚本如下:
```
cd bash-4.2
./configure --prefix=/usr --bindir=/bin --enable-static-link --without-bash-malloc
make
make install DESTDIR=$SYSROOT
```
最后得到了类似书上的结果: 一个拥有bash的命令行操作系统, 没有账户管理功能,  不支持网络, 没有很多很多常见的命令, 最原始的操作系统.

总结: 
1. 复杂的步骤一定要脚本化, 因为基本上不可能一次编译就成功.
2. 不要急, 慢慢来. 出了问题, 看看自己有没有写错；换换版本也许就OK了.
3. 书上描述的与实际或多或少有些出入, 不要埋怨作者, 放平心态. 作者的方法/思路, 操作背后的原理比结果要重要得多.


---- 2016-10-22 补充
由于系统使用的是glibc版本为2.19, 而编译bash过程中默认链接的是系统的lib, 版本不同,
动态链接的bash在initramfs中使用的是glibc-2.15编译的so文件,所以无法启动.


---- 2016-10-25 补充
经过差不多两个星期的折腾, 到底还是在64位的系统上交叉编译出32位的内核了.  将整个过程记录一下, 梳理一下.
系统的信息如下:
```
$ lsb_release -a
LSB Version:	core-2.0-amd64:core-2.0-noarch:core-3.0-amd64:core-3.0-noarch:core-3.1-amd64:core-3.1-noarch:core-3.2-amd64:core-3.2-noarch:core-4.0-amd64:core-4.0-noarch:core-4.1-amd64:core-4.1-noarch:security-4.0-amd64:security-4.0-noarch:security-4.1-amd64:security-4.1-noarch
Distributor ID:	Ubuntu
Description:	Ubuntu 14.04.4 LTS
Release:	14.04
Codename:	trusty
```
由于是在14.04系统中编译的, 那么各个待编译库的版本与系统保持一致, 说不定还容易些, 因此, 使用的各个库版本如下:
```
source/
|-- bash-4.2.tar.gz
|-- binutils-2.23.1.tar.bz2
|-- gcc-4.8.4.tar.bz2
|-- glibc-2.19.tar.xz
|-- gmp-5.0.5.tar.bz2
|-- linux-3.7.4.tar.bz2
|-- mpc-1.0.1.tar.gz
|-- mpfr-3.1.1.tar.bz2
`-- udev-173.tar.bz2
```
其中,最重要的是gcc改版本为4.8.4, glibc改版本为2.19

第一步: 配置bashrc变量
```
unset LANG
export HOST=x86_64-pc-linux-gnu
export BUILD=$HOST
export TARGET=i686-none-linux-gnu
export CROSS_TOOL=/vita/cross-tool
export CROSS_GCC_TMP=/vita/cross-gcc-tmp
export SYSROOT=/vita/sysroot
# PATH=$CROSS_TOOL/bin:/sbin:/usr/sbin:$PATH
PATH=$CROSS_TOOL/bin:$CROSS_GCC_TMP/bin:/sbin:/usr/sbin:$PATH
```
HOST变量取值`echo $MACHTYPE`, 其它不变.

第二步: 编译binutils, 内容如下:
```
$ cat s1-build-binutils.sh 
cd binutils-build
../binutils-2.23.1/configure \
--prefix=$CROSS_TOOL --target=$TARGET \
--with-sysroot=$SYSROOT
make
make install

```
第三步: 编译freestanding的gcc编译器

```
$ cat s2-build-gcc-s1.sh 
tar xvf ../source/gcc-4.8.4.tar.bz2
cd gcc-4.8.4
tar xvf ../../source/gmp-5.0.5.tar.bz2
mv gmp-5.0.5/ gmp
tar xvf ../../source/mpfr-3.1.1.tar.bz2
mv mpfr-3.1.1/ mpfr
tar xvf ../../source/mpc-1.0.1.tar.gz
mv mpc-1.0.1/ mpc
mkdir ../gcc-build 

$ cat s2-build-gcc-s2.sh 
cd gcc-build
../gcc-4.8.4/configure \
--prefix=$CROSS_GCC_TMP --target=$TARGET \
--with-sysroot=$SYSROOT \
--with-newlib --enable-languages=c \
--with-mpfr-include=/vita/build/gcc-4.8.4/mpfr/src \
--with-mpfr-lib=/vita/build/gcc-build/mpfr/src/.libs \
--disable-shared --disable-threads \
--disable-decimal-float --disable-libquadmath \
--disable-libmudflap --disable-libgomp \
--disable-nls --disable-libssp --disable-libatomic

make
make install

```
添加一个`--disable-libatomic`, 其它的没有变化.

第四步: 安装head头文件及link libgcc.a文件
```
$ cat s3-link-libgcc.sh 
cd /vita/cross-gcc-tmp/
ln -s libgcc.a lib/gcc/$TARGET/4.8.4/libgcc_eh.a
$ cat s3-install-header.sh 
cd linux-3.7.4
make ARCH=i386 INSTALL_HDR_PATH=$SYSROOT/usr/ headers_install
```

第五步: 编译glibc
```
cd glibc-build
../glibc-2.19/configure \
--prefix=/usr  --host=$TARGET \
--enable-kernel=3.7.4 --enable-add-ons \
--with-headers=$SYSROOT/usr/include \
--without-selinux \
libc_cv_forced_unwind=yes libc_cv_c_cleanup=yes \
libc_cv_ctors_header=yes libc_cv_ssp=no
make CFLAGS="-U_FORTIFY_SOURCE -O2 -fno-stack-protector"
make install_root=$SYSROOT install

```
参考http://blog.csdn.net/g__gle/article/details/8706362添加了`libc_cv_ssp=no`, 编译通过.
第六步: 再次编译gcc
```
$ cat s5-build-gcc-s3.sh 
cd gcc-build
../gcc-4.8.4/configure \
--prefix=$CROSS_TOOL --target=$TARGET \
--with-sysroot=$SYSROOT \
--enable-languages=c,c++ \
--with-mpfr-include=/vita/build/gcc-4.8.4/mpfr/src \
--with-mpfr-lib=/vita/build/gcc-build/mpfr/src/.libs \
--enable-threads=posix
make && make install

```

第七步: 配置bashrc
```
export CC="$TARGET-gcc"
export CXX="$TARGET-g++"
export AR="$TARGET-ar"
export AS="$TARGET-as"
export RANLIB="$TARGET-ranlib"
export LD="$TARGET-ld"
export STRIP="$TARGET-strip"
```

第八步: 按照书中的描述编译内核

注意编译内核的各种选项与书中保持一致, 我有一次漏掉了`AHCI SATA support`, 
结果就出现错误`Cannot open root device "(sda3)" or unknown-block(0,0): error -6`. 



------- 2016-10-26 内核编译由浅入深
1. 将硬盘驱动编译进内核, 编译内核直接从硬盘上启动
2. 编译内核支持initramfs, 为将驱动从内核剥离作准备
3. 编译udev, 将硬盘驱动编译的模块, 使用udev加载模块
4. 编码switch_root, 引导内核从initramfs内存文件系统switch到硬盘 
5. 编译内核支持网络, 安装openssh

整个过程中需要编译安装多个工具, 我都用busybox替代了.
switch_root是自行编译的, 没有使用busybox中的工具.
switch_root的第二个参数, 即/sbin/init需要有可执行权限才能在switch_root.c中调用execlp函数.















