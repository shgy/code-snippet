#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>
#include<unistd.h>

/**
 * 最后6个字符必须为XXXXXX,
 * 使用unlink 确认执行close(fd)后, 文件被删除.
 *
 * */


int main(int argc, char *argv[])
{
 int fd;
 int ret;  
 char file[] = "hello.tmp.XXXXXX";

 fd = mkstemp(file);

 if( fd < 0 )
 {
 	printf("Mkstemp Error: %s\n",strerror(errno));
	exit(1);
 }

 sleep(6);
 printf("after sleep\n");

 if( unlink(file) < 0 )
 {
 	printf("Unlink Error: %s\n", strerror(errno));
 }

 if( close(fd) == -1 )
 {
 	printf("Close Error: %s\n", strerror(errno));
	exit(1);
 }

 return 0;
}
