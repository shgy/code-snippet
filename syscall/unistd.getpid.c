#include<unistd.h>
#include<stdio.h>
/*
 * 进程id的生成规则: 一旦进程号达到32767的限制,内核将重置进程号计数器为300
 * 可用如下的方法验证
 * 1 编译程序
 *   $ gcc func_getpid.c
 * 2 运行脚本
 *   $ sh func_getpid_check.sh | grep -A3 32767
 *   32767
 *   300
 *   301
 * 
 *
 * 注意: getpid() 和 getppid() 函数的不用判断失败, 因为它们 "Always successfully returns process ID of caller"
   getpid()系统调用返回当前进程的tgid值, 而不是pid的值. 理解这句话需要理解Linux线程相关的知识.
   tgid表示 thread group id
 * */
int main(int argc, char *argv[])
{
 pid_t pid;
 pid = getpid();
 printf("%d\n", pid);
 return 0;
}
