/*
  基本内联汇编
  调用 0x80系统调用来打印字符串, 将返回的结果(字符串的长度)存储到count变量中
*/
#include<stdio.h>
char* str="hello  world\n";
int count = 0;
int main(int argc, char* argv[])
{


    asm("pusha; \
         movl $4, %eax; \
         movl $1, %ebx; \
         mov str, %ecx; \
         mov $13, %edx; \
         int $0x80; \
         mov %eax, count; \
         popa");
    printf("count=%d\n",count);
    return 0;
}