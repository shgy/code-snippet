/*
 
 chroot让我想到了docker, 想到了virtualenv. 确实, chroot最典型的应用就是隔离.
 chroot需要特权及进程, 因此执行的时候, 使用sudo 
 */

#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>
#include<limits.h>

#include<unistd.h>

int main(int argc, char *argv[])
{
 if( argc !=2 )
 {
 	printf("usage %s pathname\n", argv[0]);
	exit(1);
 }

 if( chroot(argv[1]) == -1)
 {
   printf("chroot error: %s\n", strerror(errno));
   exit(1);
 }

 char buf[PATH_MAX+1];

 if( getcwd(buf, sizeof(buf)) == NULL)
 {
 	printf("getcwd error: %s\n", strerror(errno));
	exit(1);
 }

 // 使用mkdir troot && sudo ./a.out troot 会 输出 带有 unreachable 的 字符串
 // 很容易理解,调用chroot后, 进程执行的路径在新的root之外.
 printf("cwd is %s\n", buf);
 return 0;
}
