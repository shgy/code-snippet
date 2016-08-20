#include<stdio.h>
#include<string.h>
#include<errno.h>

#include<unistd.h>
#include<signal.h>

void signalHandler(int signo)
{
   switch (signo){
      case SIGALRM:
         printf("Caught the SIGALRM signal!\n");
         alarm(1);
		 break;
     }
}
int main(int argc, char *argv[])
{

 signal(SIGALRM, signalHandler);
 
 alarm(1);
 for(;;);
 return 0;
}
