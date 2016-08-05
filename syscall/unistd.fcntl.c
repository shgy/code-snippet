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
 * 使用fcntl 检测文件是否可写 
 *
 * */

int main(int argc, char *argv[])
{
 
 int fd;
 int ret;

 int flags, access_mode;
  
 if( argc != 2 )
 {
 	printf("Usage %s file\n", argv[0]);
	exit(1);
 }

 fd = open(argv[1], O_WRONLY);

 if( fd < 0 )
 {
    printf("Open Error: %s\n", strerror(errno));
	exit(1);
 }
 
 flags = fcntl(fd, F_GETFL);

 if( flags < 0 )
 {
   printf("Fcntl Error: %s\n", strerror(errno));
   exit(1);
 }

 access_mode = flags & O_ACCMODE;
 printf("access_mode is %d\n", access_mode);
 
 // 由于fd是以O_WRONLY方式打开的,所以是writable; 如果是以O_RDONLY方式打开,则是 not writable
 if( access_mode == O_WRONLY || access_mode == O_RDWR)
 {
    printf("File %s is writable\n", argv[1]);
 }else{

   printf("File %s is not writable\n", argv[1]);
 }

 ret = close(fd);

 if( ret <0 )
 {
   printf("Close Error: %s\n", strerror(errno));
   exit(1);
 }

 return 0;
}
