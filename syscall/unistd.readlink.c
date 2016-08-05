#include<stdio.h>
#include<string.h>
#include<stdlib.h>
#include<errno.h>

#include<unistd.h>
#include<limits.h>

/*
  使用ln -s source dest 创建软链接	
 */

int main(int argc, char *argv[])
{
 
 if( argc != 2 )
 {
 	printf("usage %s pathname\n", argv[0]);
	exit(1);
 }

 char buffer[PATH_MAX+1];
 ssize_t n_read = readlink(argv[1], buffer, PATH_MAX);

 if( n_read == -1 )
 {
 	printf("readlink error: %s\n", strerror(errno));
	exit(1);
 }
 buffer[n_read] = 0;
 printf("buffer=%s\n", buffer);

 return 0;
}
