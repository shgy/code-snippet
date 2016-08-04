#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

#include<unistd.h>
/**
 *
 * */
int main(int argc, char *argv[])
{
 char *name;
 for(int i=0;i<3;i++)
 {
 
   name = ttyname(i);
   if( name == NULL )
   {
      printf("ttyname error: %s\n", strerror(errno));
	  exit(1);
   }
   printf("ttyname of [%d] is %s\n",i, name); 
 }
 return 0;
}
