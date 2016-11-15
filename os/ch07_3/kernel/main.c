#include "print.h"
#include "init.h"
struct page{
   uint32_t a;
   uint32_t b;
};
void main(void) {
    put_str("I am kernel\n");
    init_all();
   // asm volatile("sti");	     // 为演示中断处理,在此临时开中断
    struct page *p;
    p=(void* )0x200000;
    p->a=10;
    p->b=20;
    put_int(sizeof(struct page));
    put_str("\nover");
   while(1);
}