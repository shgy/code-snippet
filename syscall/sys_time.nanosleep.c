/*
 nanosleep()的实现并未使用信号.
 功能: 
     当休眠被中断, 利用参数remain再次进入休眠,直到耗尽全部的休眠时间
 */

#include<stdio.h>
#include<string.h>
#include<errno.h>

#include<signal.h>
#include<sys/time.h>

static void signalHandler(int sig)
{
   printf("signal handler\n");
   return;
}

int main(int argc, char *argv[])
{
 struct timeval start, finish;
 struct timespec request, remain;
 int s;
 
 signal(SIGINT, signalHandler);

 request.tv_sec = atol(argv[1]);
 request.tv_nsec = atol(argv[2]);
 
 gettimeofday(&start, NULL);
 
 for(;;)
 {
   s = nanosleep(&request, &remain);
   //如果是其它信号引起的中断, 则退出
   if(s == -1 && errno != EINTR)
   {
     printf("nanosleep error: %s\n",strerror(errno));
	 return 1;
   }

   gettimeofday(&finish, NULL);
   
   printf("Slept for: %9.6f secs\n", finish.tv_sec - start.tv_sec + (finish.tv_usec - start.tv_usec ) / 1000000.0);

   if(s==0) break;

   printf("Remaining: %2ld.%09ld\n", (long) remain.tv_sec, remain.tv_nsec);
   request = remain;
 }

 printf("complete\n");

 return 0;
}
