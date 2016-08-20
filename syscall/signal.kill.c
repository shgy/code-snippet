#include<stdio.h>
#include<string.h>
#include<stdlib.h>
#include<errno.h>
/*
 使用kill 检测进程是否存在
 ./a.out pid
 */
#include<signal.h>

int main(int argc, char *argv[])
{

 int pid = atoi(argv[1]);
 if( kill(pid,0) == -1)
 {
   perror("kill:");
   return 1;
 }

 printf("process do exists\n");
 return 0;
}
