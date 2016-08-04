#include<stdio.h>
#include<sys/time.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

/*
 * gettimeofday()的第二个参数是tz是历史产物.
 * 目前已经废弃, 始终将其置为NULL即可.
 * */

int main(int argc, char *argv[])
{
 struct timeval tv;
 int ret;
 ret = gettimeofday(&tv, NULL);
 if( ret < 0 )
 {
 	printf("Gettimeofday Error: %s\n",strerror(errno));
	exit(1);
 }

 printf("Seconds since Epoch: %d.%d\n", tv.tv_sec, tv.tv_usec);
 return 0;
}
