#define _GNU_SOURCE
#include<stdio.h>
#include<sys/utsname.h>
#include<stdlib.h>

int main(int argc, char *argv[])
{
 struct utsname uts;
 if(uname(&uts) == -1){
   perror("uname");
   exit(1);
 }

 printf("Node name: %s\n", uts.nodename);
 printf("System name: %s\n", uts.nodename);
 printf("Release: %s\n", uts.release);
 printf("Version: %s\n", uts.version);
 printf("Machine: %s\n", uts.machine);
 
 #ifdef _GNU_SOURCE
   printf("Domain name : %s\n", uts.domainname);
 #endif


 return 0;
}
