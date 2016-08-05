#include<stdio.h>
#include<stdlib.h>
#include<string.h>

#include<errno.h>
#include<unistd.h>
/**
 *
 *
 *
 * */
int main(int argc, char *argv[])
{
 char *ptr;
 ptr = malloc(1024* sizeof(char));
 if( ptr == NULL )
 {
 	printf("Malloc Failed: %s\n", strerror(errno));
	exit(1);
 }

 printf("Malloc success!\n");
 
 free(ptr);ptr=NULL;
 end = sbrk(0);
 printf("Heap size is %d\n",(end - start));
 return 0;
}
