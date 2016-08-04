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
 *  将数据写入到文件中. 如果文件存在, 则输出错误,然后退出.
 *  关键点在于 验证文件是否存在 必须是一个 "原子操作" 
 *  即 使用 O_CREAT | O_EXCL
 *
 *  write 不会保证数据已经写入磁盘. 因为内核会缓冲磁盘的I/O操作.
 * */
int main(int argc, char *argv[])
{
  int fd, ret;
  ssize_t n_write = 0;
  ssize_t n_size; 
  char *r_file;

  if( argc != 3 ){
     printf("Usage %s file message\n", argv[0]);
	 exit(1);
  }

  r_file = argv[1];
  


  fd = open(r_file, O_WRONLY | O_CREAT | O_EXCL);
  if( fd<0 ){
	printf("Open Error: %s\n", strerror(errno));
    exit(1);
  }

  printf("FD is %d\n", fd);
  n_size = strlen(argv[2]);
  n_write = write(fd, argv[2], n_size);
    
    if ( n_write < 0 )
    {
      printf("Write Error: %s", strerror(errno));
      exit(1);
    }
	printf("Write Success\n");

  ret = close(fd);
  
  if ( ret<0 ){
    printf("Close Error: %s\n", strerror(errno));
	exit(1);
  }
 return 0;
}
