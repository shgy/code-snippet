#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

#include<unistd.h>
#include<fcntl.h>

int main(int argc, char *argv[])
{
 if(fork() !=0) /* Exit if parent, or on error */
 {
   exit(EXIT_SUCCESS);
 }

 if( setsid() == -1)
 {
   perror("setsid");
   exit(1);
 }

 printf("PID=%ld, PGID=%ld, SID=%ld\n", (long) getpid(), (long) getpgrp(), (long) getsid(0));
 
 if(open("/dev/tty", O_RDWR) == -1)
 {
   perror("open /dev/tty");
   exit(1);
 }
 return 0;
}
