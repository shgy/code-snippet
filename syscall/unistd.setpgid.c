#include<stdio.h>
#include<string.h>
#include<stdlib.h>
#include<errno.h>
#include<unistd.h>

int main(int argc, char *argv[])
{
  if(setpgid(getpid(),5210) == -1)
  {
     perror("setgrid error");
	 exit(1);
  }
  printf("pgid is %d\n", getpgrp());
 return 0;
}
