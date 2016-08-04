#include<stdio.h>
#include<string.h>
#include<stdlib.h>

int main(int argc, char *argv[])
{

 int num;
 if( argc !=2 )
 {
   printf("Usage %s num\n", argv[0]);
   exit(1);
 }

 num = atoi(argv[1]);
 printf("num=%d\n",num);
 return 0;
}
