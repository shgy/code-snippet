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
 * 读取文件文本的最后1个字符
 *
 * */

int main(int argc, char *argv[])
{
 
 int fd;
 char buf[RD_BUF_SIZE];
 size_t r_count = RD_BUF_SIZE - 1;
 ssize_t n_read;
 int ret;
 off_t off;

 if( argc != 2 )
 {
 	printf("Usage %s file\n", argv[0]);
	exit(1);
 }

 fd = open(argv[1], O_RDONLY);

 if( fd < 0 )
 {
    printf("Open Error: %s\n", strerror(errno));
	exit(1);
 }
 
 off = lseek(fd, -2, SEEK_END);
 
 if( off < 0 )
 {
   printf("Lseek Error: %s\n", strerror(errno));
   exit(1);
 }
 
 n_read = read(fd, buf, 1);

 if( n_read < 0 )
 {
  	printf("Read Error: %s\n", strerror(errno));
	exit(1);
 }

 buf[n_read]=0;

 printf("Last Byte is : %s\n", buf);
 
 ret = close(fd);

 if( ret <0 )
 {
   printf("Close Error: %s\n", strerror(errno));
   exit(1);
 }

 return 0;
}
