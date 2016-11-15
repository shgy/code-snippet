#include<stdio.h>
#include<stdlib.h>

int main(int argc, char *argv[])
{
 char *msg = getenv("HADOOP_OPTS");
 printf("%s\n", msg);
 return 0;
}
