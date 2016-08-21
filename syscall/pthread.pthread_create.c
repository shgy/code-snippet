/*
 
 $ gcc pthread.pthread_create.c -lpthread 
 最简单版本的pthread_create 的用法
 有资料说, pthread_create是基于clone函数实现的. 使用
 strace 可以看到确实有使用clone函数

 另: 使用ps -efL 命令可以看到线程的相关信息
 */

#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

void *start()
{
  int i;
  for(i=0;i<10;i++)
  {
   printf("thread start\n");
   sleep(1);
  }
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
