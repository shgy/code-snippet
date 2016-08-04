#include<stdio.h>
#include<stdlib.h>

int main(int argc, char *argv[])
{
 char *msg = getenv("JAVA_HOME");
 printf("%s\n", msg);
 return 0;
}
