#include<stdio.h>
#include<stdlib.h>
#include<stdio.h>
#include<sys/types.h>
#include<sys/stat.h>
#include<fcntl.h>
#include<errno.h>
#include<string.h>
#include<unistd.h>
/**
 *  读取一个已经存在的文本; utf-8 编码.
 *  由于不会创建文件, 所以 open() 函数的mode可以忽略.
 *  由于表示字符串终止的空字符需要一个字节, 因此缓冲区的大小 至少要比预计读取的最大字符串长度多出一个字节.
 * */
#define RD_BUF_SIZE 1025
int main(int argc, char *argv[])
{
  int fd, ret;
  char buf[RD_BUF_SIZE]= {0};
  ssize_t r_size = RD_BUF_SIZE -1 ;
  ssize_t n_read = 0;
  
  char *r_file;

  if( argc != 2 ){
     printf("Usage %s file\n", argv[0]);
	 exit(1);
  }

  r_file = argv[1];

  fd = open(r_file, O_RDONLY);
  if( fd<0 ){
	printf("Open Error: %s\n", strerror(errno));
    exit(1);
  }

  printf("FD is %d\n", fd);
  
  do{
    n_read = read(fd, buf, r_size);
    
    if ( n_read < 0 )
    {
      printf("Read Error: %s", strerror(errno));
      exit(1);
    }
    buf[n_read] = 0;
	printf("%s\n", buf);

  }while( n_read > 0 );

  ret = close(fd);
  
  if ( ret<0 ){
    printf("Close Error: %s\n", strerror(errno));
	exit(1);
  }
 return 0;
}
