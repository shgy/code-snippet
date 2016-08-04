#include<stdio.h>
#include<string.h>
#include<errno.h>
#include<stdlib.h>
#include<unistd.h>

#include<sys/types.h>
#include<sys/stat.h>
#include<fcntl.h>

#define RD_BUF_SIZE 1025
/**
 * 使用dup实现 shell 2>&1 重定向的功能 
 * 
 * $ gcc func_dup.c
 * $ ./a.out > /dev/null
 * before dup stderr
 *
 * 只输出了这一行, 表明执行dup(2)后, 原来指向stderr的文件描述符2指向了stdout. 
 * */

int main(int argc, char *argv[])
{
 int ret ;
 int newfd;

 fprintf(stdout,"before dup stdout \n");
 fprintf(stderr,"before dup stderr\n");

 ret = close(2);

 if( ret < 0 )
 {
   printf("Close Error: %s\n", strerror(errno));
   exit(1);
 }

 newfd = dup(1);

 if( newfd <0 )
 {
   printf("Dup Error: %s\n", strerror(errno));
   exit(1);
 }
 stderr->_fileno=newfd;


 if( newfd == 2 )
 {
   fprintf(stdout,"after dup stdout \n");
   fprintf(stderr,"after dup stderr \n");
 
 }else{
   printf("New FD is %d\n", newfd);
 }



 

 return 0;
}
