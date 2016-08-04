#include <stdio.h>
#include<stdlib.h>
#include <string.h>
#include <errno.h>
int main(void)
{ 
   for (int i = 0; i < 136; ++i)
   {
    
     char * mesg = strerror(i);
     printf( "Mesg [%d]:%s\n", i, mesg);

   }
  
   exit(0);
}
