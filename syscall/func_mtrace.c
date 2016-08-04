#include <stdio.h>
#include <stdlib.h>
#include <string.h>
 
#include <mcheck.h>
 
/*
 * $ gcc func_mtrace.c -g -o mtrace.out
 * $ mtrace mtrace.out output
 * - 0x0000000000610010 Free 3 was never alloc'd 0x7f666babbfaa
 * - 0x00000000006102d0 Free 4 was never alloc'd 0x7f666bb76a6d
 * - 0x00000000006102f0 Free 5 was never alloc'd 0x7f666bbe500c
 *
 * Memory not freed:
 * -----------------
 *            Address     Size     Caller
 * 0x0000000000610770     0x64  at /home/shgy/github-public/code-snippet/syscall/func_mtrace.c:12
 * */ 
int main(){
     setenv("MALLOC_TRACE", "output", 1);
     mtrace();
		  
     char * text = ( char * ) malloc (sizeof(char) * 100);
     memset(text,'\0',100);
     memcpy(text,"hello,world!",12);
		  
     printf("%s/n",text);
     return 0;
}
