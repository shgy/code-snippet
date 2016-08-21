#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

#include<pthread.h>
/*
   pthread_join 自己返回的结果为: Operation not permitted
 */

int main(int argc, char *argv[])
{
 pthread_t t = pthread_self();
 printf("pthread_self: %u\n", (unsigned int)t ); 
 int ret;
 if( ret = pthread_join(pthread_self(),NULL) !=0)
 {
   printf("pthread_join:%s\n",strerror(ret));
 }
 printf("after pthread_join\n");
 return 0;
}
