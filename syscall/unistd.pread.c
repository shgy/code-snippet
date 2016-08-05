#include<stdio.h>
#include<stdlib.h>
#include<errno.h>
#include<string.h>

#include<sys/types.h>
#include<sys/stat.h>
#include<fcntl.h>
#include<unistd.h>
/**
 * pread() 是原子操作
 *
 * 在编程中,可能会遇到这样的需求:跳过一些固定的长度,读取文件的内容.
 * 比如: 跳过开头n个字符,读取文件
 *
 * 这一般用于读取特殊设计的文件.比如MySQL的数据库文件/Lucene的索引文件
 * */
int main(int argc, char *argv[])
{
 
  int fd, ret;
  char buf[1025]={0};
  size_t count = 1024;
  ssize_t n_read;
  off_t offset;

  if (argc !=3 )
  {
    printf("Usage %s file offset\n", argv[0]);
	exit(1);
  }

  fd = open(argv[1], O_RDONLY);
  
  offset = atoi(argv[2]);

  if (fd < 0 )
  {
  	printf("Open Error: %s\n", strerror(errno));
	exit(1);
  }

  n_read = pread(fd, buf, count, offset);
  
  if( n_read < 0 )
  {
  	printf("PRead Error: %s\n", strerror(errno));
	exit(1);
  }

  buf[n_read] = 0;

  printf("%s\n", buf);
 


  ret = close(fd);

  if( ret < 0)
  {
  	printf("Close Error: %s\n", strerror(errno));
	exit(1);
  }



 return 0;
}
