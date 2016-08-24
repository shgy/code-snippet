#include<stdio.h>
#include<unistd.h>

int main(int argc, char *argv[])
{
 int val = sysconf(_SC_PAGE_SIZE);
 int pa_offset = 8193 & ~(sysconf(_SC_PAGE_SIZE) - 1);
 printf("page_size is %d: %d\n",val, pa_offset );
 return 0;
}
