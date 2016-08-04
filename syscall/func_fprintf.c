#include<stdio.h>

int main(int argc, char *argv[])
{
 
 while(1)
 {
   fprintf(stderr, "%s\n","hello stderr" );
   fprintf(stdout, "%s\n","hello stdout" );
   sleep(1);
 }

 return 0;
}

