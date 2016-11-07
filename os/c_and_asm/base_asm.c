#include<stdio.h>
int in_a = 1, in_b = 2, out_asm;
int main(int argc, char* argv[])
{
   asm("pusha; \
        movl in_a, %eax; \
        movl in_b, %ebx; \
        addl %ebx, %eax; \
        movl %eax, out_asm; \
        popa");

   printf("sum is %d\n", out_asm);

   return 0;
}