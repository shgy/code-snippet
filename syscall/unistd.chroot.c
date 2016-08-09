/*
 参考: http://www.ibm.com/developerworks/cn/linux/l-cn-chroot/
 chroot让我想到了docker, 想到了virtualenv. 确实, chroot最典型的应用就是隔离.
 chroot需要特权级进程, 因此执行的时候, 使用sudo 
 $ gcc unistd.chroot.c
 $ mv a.out chroot_cmd_example && cd chroot_cmd_example
 $ sudo ./a.out
 即可得到与chroot命令一样的效果
 */
#include<unistd.h>

#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

int main(int argc, char *argv[])
{
 if( chroot(".") == -1 )
 {
 	printf("chroot failed %s\n",strerror(errno));
	exit(1);
 }
 if( chdir("/") == -1 )
 {
   printf("chdir failed %s\n",strerror(errno));
   exit(1);
 }

 char *arrays[] = {"ash", NULL};
 if( execvp("ash", arrays) == -1 )
 {
   printf("execvp failed %s\n",strerror(errno));
   exit(1); 
 }
 
 return 0;
}
