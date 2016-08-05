#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

#include<unistd.h>

/*
	函数open()会解链接, 打开symlink指向的文件；
	如果要打开symlink文件本身, 则需要使用readlink()函数
    
	需要注意的是: 即使filepath指向的文件不存在, 函数也会正常执行
 */

int main(int argc, char *argv[])
{
 if( argc !=3 )
 {
 	printf("Usage %s filepath linkpath\n", argv[0]);
	exit(1);
 }

 if( symlink(argv[1], argv[2]) == -1 )
 {
   printf("Symlink Error: %s\n", strerror(errno));
   exit(1);
 }

 return 0;
}
