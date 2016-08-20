/*
 参考: http://blog.csdn.net/wangpengqi/article/details/8992907
 gcc time.clock_gettime.c -lrt
*/
#include <stdio.h>
#include <time.h>
int main()
{
	struct timespec time1 = {0, 0};
	clock_gettime(CLOCK_REALTIME, &time1);
	printf("CLOCK_REALTIME: %d, %ld\n", (int)time1.tv_sec, time1.tv_nsec);
	clock_gettime(CLOCK_MONOTONIC, &time1);
	printf("CLOCK_MONOTONIC: %d, %ld\n", (int)time1.tv_sec, time1.tv_nsec);
	clock_gettime(CLOCK_PROCESS_CPUTIME_ID, &time1);
	printf("CLOCK_PROCESS_CPUTIME_ID: %d, %ld\n", (int)time1.tv_sec, time1.tv_nsec);
	clock_gettime(CLOCK_THREAD_CPUTIME_ID, &time1);
	printf("CLOCK_THREAD_CPUTIME_ID: %d, %ld\n", (int)time1.tv_sec, time1.tv_nsec);
	printf("\n%ld\n", time(NULL));
	sleep(1);
}
