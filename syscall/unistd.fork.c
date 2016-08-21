#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

#include<unistd.h>

/*
  fork 即叉子. 这样联想起来就比较容易理解.
  fork 出来的两个进程, 一般都是父进程先执行, 而子进程后执行.
*/

int main(int argc, char *argv[])
{
 pid_t pid;

 switch(pid = fork())
 {
   case -1:
	perror("fork:");
	return 1;
	break;
   case 0:
	printf("I'm child process\n");
    break;
   default:
    printf("I'm parent process\n");
	sleep(1); // 父进程 先执行, 所以需要等待
	break;
 }
 return 0;
}
