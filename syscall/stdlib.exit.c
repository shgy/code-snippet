#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

#include<unistd.h>
#include<sys/wait.h>
/*
 TLPI ch-25 exercise

 */
int main(int argc, char *argv[])
{
 int status; 
 switch (fork()) {
  case -1:
    perror("fork:");
	return 1;
  case 0:
	printf("I'm child\n");
    exit(-1);
  default:
	wait(&status);
	printf("the return code is %d\n",WEXITSTATUS(status));
	return 0;
   
 }

 return 0;
}
