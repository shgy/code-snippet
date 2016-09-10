#include<stdio.h>
#include<unistd.h>
/*
  名称为getpgrp()而不是getpgid()是有历史原因的.
  getpgid(pid)在Linux中是可以使用的
*/
int main(int argc, char *argv[])
{
 pid_t pgid;
 pgid = getpgrp();
 printf("process group id is :%d\n", pgid);
 pgid = getpgid(getpid());
 printf("process group id is :%d\n", pgid);
 return 0;
}
