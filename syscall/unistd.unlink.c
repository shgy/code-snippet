#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>
#include<unistd.h>
/*
 
   unlink 不会对符号链接进行解引用操作
   unlink 不能移除目录
   如果文件有打开的文件描述符, 则unlink()函数不会删除该文件. 故程序可以对文件进行正常读写操作.
   由于无法将文件名与i-node关联, 因此该文件可以视为被删除.
   tmpfile()函数 利用了该特性.
   不用担心磁盘空间占用的问题: 程序停止后, OS会清理.
 */

int main(int argc, char *argv[])
{
 if( argc != 2)
 {
 	printf("Usage %s file",argv[0]);
	exit(1);
 }

 if( unlink(argv[1]) == -1 )
 {
 	printf("Unlink Error: %s\n", strerror(errno));
	exit(1);
 }

 printf("Unlink Success\n");
 return 0;
}
