#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include<errno.h>
#include<unistd.h>
/**
 *
 * malloc和free是一对如影随行的兄弟.
 *
 * 注意点在于: 使用free(ptr)后, 将ptr置为NULL是一个好习惯. 
 *
 * */
int main(int argc, char *argv[])
{
 int start, end;
 char *ptr; 
 ptr = malloc(1024* sizeof(char));
 if( ptr == NULL )
 {
 	printf("Malloc Failed: %s\n", strerror(errno));
	exit(1);
 }

 printf("Malloc success!\n");
 
 free(ptr);ptr=NULL;

 return 0;
}
