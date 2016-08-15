#include<stdio.h>
#include<string.h>
#include<errno.h>

#include<signal.h>

int main(int argc, char *argv[])
{
 int i;
 for(i=1; i<32; i++)
 {
   printf("%s\n", strsignal(i));
 }
 return 0;
}
