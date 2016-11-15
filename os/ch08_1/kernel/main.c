#include "print.h"
#include "init.h"
#include "debug.h"

int main(void) {
    put_str("I am kernel\n");
    init_all();
   // asm volatile("sti");	     // 为演示中断处理,在此临时开中断
   ASSERT(2==1)
    struct page *p;
   while(1);
   return 0;
}