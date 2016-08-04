使用shell 命令`nohup my_job.sh > /dev/null 2>&1 &` 执行任务是非常常见的操作.
通过这样的方式执行命令, 即退出当前shell会话, 任务也不会停止.

Linux每创建一个进程, 会自动为该进程创建三个Stream, 即: 标准输入(stdin)/标准输出(stdout)/标准错误(stderr).
对应的文件描述符分别是0 1 2.
可用如下的代码验证:
```
#include<stdio.h>

int main(int argc, char *argv[])
{

 printf("FD of stdin is: %d\n", stdin->_fileno);
 printf("FD of stdout is: %d\n", stdout->_fileno);
 printf("FD of stderr is: %d\n", stderr->_fileno);
 
 return 0;
}

```


对于最开始的命令`nohup my_job.sh > /dev/null 2>&1 &`,暂时不考虑表示不挂起(no hang up)的nohup以及表示后台执行的& .
就只剩下`my_job.sh > /dev/null 2>&1 `

`my_job.sh > /dev/null` 表示将标准输出重定向到/dev/null文件, 而/dev/null会丢弃所有写入的数据. 那么命令即表示忽略标准输出.

验证代码如下:
```
$ cat func_fprintf.c
#include<stdio.h>

int main(int argc, char *argv[])
{
 
 while(1)
 {
   fprintf(stderr, "%s\n","hello stderr" );
   fprintf(stdout, "%s\n","hello stdout" );
   sleep(1);
 }

 return 0;
}

$ gcc func_fprintf.c
$ ./a.out > /dev/null    # equal to ./a.out 1> /dev/null
hello stderr
hello stderr
$ ./a.out 2> /dev/null
hello stdout
hello stdout

```

而`2>&1`表示将标准错误重定向到标准输出. 联合起来 `2>&1 >/dev/null`即表示同时忽略标准输出和标准错误. 
换句话说,就是终端(terminal)不输出任何信息. 
`2>&1 > my_job.out` 即表示标准输出和标准错误都输出到my_job.out文件.

=====================================================================



