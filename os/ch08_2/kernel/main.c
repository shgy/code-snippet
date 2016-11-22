#include "print.h"
#include "init.h"
#include "string.h"
int main(void) {
   put_str("I am kernel\n");
   init_all();

   char a[10]={'a',};
   char *b="123";
   strcat(a,b);

   put_str(a);

   while(1);
   return 0;
}