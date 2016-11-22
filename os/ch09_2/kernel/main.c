#include "print.h"
#include "init.h"
#include "thread.h"

void k_thread_a(void*);
int count=1;
int main(void) {

   put_str("I am kernel\n");
   init_all();

   thread_start("k_thread_a", 31, k_thread_a, "argA ");

   while(count++){

      if(count%100000==0){
         put_str("main =====10000====");
         count=1;
      }
   };
   return 0;
}

/* 在线程中运行的函数 */
void k_thread_a(void* arg) {
/* 用void*来通用表示参数,被调用的函数知道自己需要什么类型的参数,自己转换再用 */
   char* para = arg;
   while(1) {
      put_str(para);
   }
}
