/*
 touch test.txt
 ln -s test.txt test.txt.lnk
 ./a.out test.txt.lnk
 */
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

#include<libgen.h>
#include<limits.h>


int main(int argc, char *argv[])
{
 if( argc !=2 ){
 	printf("usage %s pathname\n", argv[0]);
	exit(1);
 }

 char buf[PATH_MAX+1];
 if( realpath(argv[1], buf) ==NULL)
 {
 	printf("realpath error: %s\n",strerror(errno));
	exit(1);
 }
 printf("realpath is %s\n", buf);

 return 0;
}
