#include<stdio.h>
#pragma pack (2)
struct A{
  short int b;
  long a; 
  char c;
};
#pragma pack ()
int main(int argc, char *argv[]){
   printf("long=%d\n",(unsigned)sizeof(long));
   printf("int=%d\n",(unsigned)sizeof(int));
   printf("short=%d\n",(unsigned)sizeof(short));
   printf("char=%d\n",(unsigned)sizeof(char));
   printf("size of struct A is: %d\n",(unsigned)sizeof(struct A));

   return 0;
}
