#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

#include<pthread.h>
/*
   线程的同步: 使用互斥量
   顺便说一句: 在linux中, 互斥量是使用futex系统调用实现的; 使用strace命令确实看到了futex的身影.
 */

static int glob = 0;
static pthread_mutex_t mtx = PTHREAD_MUTEX_INITIALIZER;


void *start(void *arg)
{
  int loops = *((int *) arg);
  int loc, j, s;
  
  for( j=0;j<loops;j++)
  {
    s = pthread_mutex_lock(&mtx);
	if (s != 0)
	{
	  perror("pthread_mutex_lock:");
	  pthread_exit((void *)1);
	}
	loc = glob;
	loc++;
	glob = loc;
	s=pthread_mutex_unlock(&mtx);
	if( s!= 0)
	{
	  perror("pthread_mutex_unlock:");
	  pthread_exit((void *)1);
	}
  }
  pthread_exit(NULL);  
}

int main(int argc, char *argv[])
{
 
 pthread_t t1, t2;

 int loops, s;

 if( argc !=2)
 {
   printf("Usage %s count\n", argv[0]);
   return 1;
 }
 loops = atoi(argv[1]);

 s = pthread_create(&t1, NULL, start, &loops);
 if(s!=0)
 {
   perror("pthread_create:");
   return 1;
 }

 s = pthread_create(&t2, NULL, start, &loops);
 if(s!=0)
 {
   perror("pthread_create:");
   return 1;
 }

 s = pthread_join(t1, NULL);

 if(s!=0)
 {
   perror("pthread_join:");
   return 1;
 }
 s = pthread_join(t2, NULL);

 if(s!=0)
 {
   perror("pthread_join:");
   return 1;
 }
 printf("glob = %d\n", glob);
 return 0;
}
