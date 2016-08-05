#include<stdio.h>
#include<unistd.h>

int main(int argc, char *argv[])
{
 char *passwd = getpass("Please Input Passwd:");
 printf("Passwd is : %s\n", passwd);
 return 0;
}
