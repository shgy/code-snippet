#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>
#include<signal.h>
/*
 signal()函数用于改变信息的处置方式. 
 例如: Linux系统中大部分
 信号处置的默认行为是程序退出. 但是一些程序, 比如Vim,为了防止
 误操作, ctrl+c后并不立即退出,而是等待用户输入entel确认.
 这就需要修改该程序对信号的响应方式.

 需要注意的是, signal在不同的系统,实现方式不同. 因此, 如果程序有
 跨平台需求时, 不能使用signal()方法.
 */
static void sigHandler(int sig)
{
	printf("Ouch!\n");
}

int main(int argc, char *argv[])
{
 if(signal(SIGINT, sigHandler) == SIG_ERR)
 {
 	perror("signal");
	return 1;
 }
 int i=0;
 for(;i<3;i++)
 {
 	printf("%d\n",i);
	sleep(3);
 }
 return 0;
}
