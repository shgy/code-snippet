#include  <stdio.h>
#include  <signal.h>
#include  <stdlib.h>

long  prev_fact, i;    

void  SIGhandler(int);     

void  SIGhandler(int sig)
{
     printf("\nReceived a SIGUSR1.  The answer is %ld! = %ld\n", 
               i-1, prev_fact);
     exit(0);
}

void  main(void)
{
     long  fact;

     printf("Factorial Computation:\n\n");
     signal(SIGUSR1, SIGhandler); 
     for (prev_fact = i = 1; ; i++, prev_fact = fact) { 
          fact = prev_fact * i;  
          if (fact < 0)        
               raise(SIGUSR1); 
          else if (i % 3 == 0)  
               printf("     %ld! = %ld\n", i, fact);
     }
}
