#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

#include<unistd.h>

int main(int argc, char *argv[])
{
 pid_t sid;
 if((sid=getsid(getpid())) == -1)
 {
   perror("getsid");
   exit(1);
 }
 printf("session id is %d\n", sid);
 return 0;
}
