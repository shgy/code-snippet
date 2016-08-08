/*
 	注意dirname和basename都可以修改pathname指向的字符串, 因此应用时:
	需要使用strdup函数复制原路径
 */
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

#include<libgen.h>


int main(int argc, char *argv[])
{
 if( argc !=2 ){
 	printf("usage %s pathname\n", argv[0]);
	exit(1);
 }

 char *t1, *t2;
 t1 = strdup(argv[1]);
 if( t1 == NULL){
 	printf("strdup failed: %s\n",strerror(errno));
	exit(1);
 }
 t2 = strdup(argv[1]);
 if( t2 == NULL){
 	printf("strdup failed: %s\n",strerror(errno));
	exit(1);
 }
 printf("dirname is : %s\n", dirname(t1));
 printf("basename is : %s\n", basename(t2));
 free(t1);
 free(t2);
 return 0;
}
