#include<stdio.h>
/*
  关于结构体的对齐, 其目的是为了CPU在取数据时, 能够更快, 提升系统的性能.
  是属于空间换时间的一种策略.

  结构体的对齐有两种方式: 
  其一默认对齐,选择结构体中最大的基础类型作为对齐长度.
  其二使用如下的 #pragma pack (n) 语法指定对齐的长度.

 * */

#pragma pack (2)
struct A{
  short int b;
  long a; 
  char c;
};
#pragma pack ()

struct B{
  char name[10];
  int *a;
  struct A s;
};

int main(int argc, char *argv[]){
   printf("long=%d\n",(unsigned)sizeof(long));
   printf("int=%d\n",(unsigned)sizeof(int));
   printf("short=%d\n",(unsigned)sizeof(short));
   printf("char=%d\n",(unsigned)sizeof(char));
   printf("size of struct A is: %d\n",(unsigned)sizeof(struct A));
   printf("size of struct A is: %d\n",(unsigned)sizeof(struct B));

   return 0;
}
