编译系统启动后,bash显示I have no name.
在/etc目录下,是有passwd文件和group文件的
想到getpwuid()函数用于读取passwd文件, 因此编写了一个简单的程序
```
# $ cat pwd.getpwuid.c 
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

#include<pwd.h>

int main(int argc, char *argv[])
{
 
 struct passwd *p1;
 
 p1 = getpwuid(0);
 
 if( p1 == NULL )
 {
 	printf("Getpwnam: %s\n", strerror(errno));
 	exit(1);
 }

 printf("%s: %s\n", p1->pw_name, p1->pw_dir);

 return 0;
}

```
编译后放到虚拟机系统中执行, 发现输出的结果为"Getpwnam: Success", 而非"root: /root"
使用strace命令查看打开的文件,发现了
"/etc/nsswitch.conf"和"/lib/i386-linux-gnu/libnss_compat.so.2"
将这两个文件复制到虚拟机文件系统中后, 再开机, bash显示正常.
将"/etc/nsswitch.conf"文件删除后, 执行"bash -l"显示正常,
将"/lib/libnss_compat.so.2"文件删除后,执行"bash -l"显示"I have no name!"

至此, 得出结论: libnss_compat.so.2动态链接库是主谋.

至于如何影响, 暂不深挖了. libnss_compat.so.2是libnss_compact-2.19.so的软链接,
而libnss_compact-2.19.so则是glibc编译出来的.



