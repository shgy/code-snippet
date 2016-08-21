/*
 
 $ gcc pthread.pthread_exit.c -lpthread 
 pthread_exit 只退出当前线程
 */

#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

void *start()
{
  int i;
  for(i=0;i<5;i++)
  {
   printf("thread start\n");
   sleep(1);
  }
  pthread_exit(NULL);
}

int main(int argc, char *argv[])
{
  pthread_t  t;
  int rt;
 
  rt = pthread_create(&t, NULL, start, NULL);
  if(rt != 0 )
  { // 由于引用errno都有一次函数调用的开销
    printf("Pthread_create: %s\n", strerror(rt));
	return 1;
  }
  int i;
  for( i=0; i<10;i++)
  {
    printf("main thread\n"); 
    sleep(1);
  }
  return 0;
}
