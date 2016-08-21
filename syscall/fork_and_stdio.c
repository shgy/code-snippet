#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>
/*
 $ ./a.out 
 Hello world
 Ciao
 $ ./a.out > a.txt && cat a.txt 
 Ciao
 Hello world
 Hello world

 现象就是输出到控制台和输出到文本中的结果不一样；
 原因解释如下:
 1 输出顺序不一样是因为: 
   stdio输出到shell是行缓冲；输出到文件是全缓冲.
 2 Hello world 输出两次是因为: 
   stdio的缓存是在用户空间的内存中；
   fork创建子进程会复制这些缓冲区；
   父子进程退出会刷新各自的stdio缓冲区

 猜想: 如果再调用一次fork, 则会输出4个hello world
 结论: 还真是如此
*/
#include<unistd.h>

int main(int argc, char *argv[])
{
 printf("Hello world\n");
 write(STDOUT_FILENO, "Ciao\n", 5);

 if(fork() == -1)
 {
   perror("Fork:");
   return 1;
 }
 fork();

 return 0;
}
