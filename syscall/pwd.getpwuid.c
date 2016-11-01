#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>

#include<pwd.h>

int main(int argc, char *argv[])
{
 
 struct passwd *p1;
 
 p1 = getpwuid(0);
 
 if( p1 == NULL )
 {
 	printf("Getpwnam: %s\n", strerror(errno));
 	exit(1);
 }

 printf("%s: %s\n", p1->pw_name, p1->pw_dir);

 return 0;
}
