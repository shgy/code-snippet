#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<time.h>
/**
 *
 * TLPI-exercise-Chapter-10 
 *
 * 使用 $ date +%s 命令可以显示出当前距Epoch的秒数
 * 关于时间的讲解, 可以参考 python/mod_time.md文章.
 *
 * 我的理解就是MVC.
 * 
 * 用time_t表示的时间就是M(Model)层.对应的函数是time()
 * 
 * 用struct tm表示的时间就是C(Controller)层.对应的函数是gmtime()/localtime()/strptime()
 * 
 * 用字符串表示的时间就V(View)层. 以最直观的方式展现,对应的函数是ctime()/asctime()/strftime()
 *
 * */

int main(int argc, char *argv[])
{
 time_t t = time(NULL);
 
 printf("seconds since 1970: %ld\n", t);
 printf("ctime             : %s\n", ctime(&t));
 
 struct tm local, gm;
 char buf[255];
 /*
  *不能将localtime()和gmtime()同时使用, 因为它们共用同一片静态内存区域
  *因此使用 localtime_r()和gmtime_r()
  * */
 localtime_r(&t, &local);
 gmtime_r(&t, &gm);
 
 printf("asctime local: %s\n", asctime(&local));
 printf("asctime    gm: %s\n", asctime(&gm));

 printf("YYYY-mm-dd HH:MM:SS 字符串时间\n");

 memset(buf,0,sizeof(buf));  
 strftime(buf, sizeof(buf), "%Y-%m-%d %H:%M:%S", &local); 
 printf("local: %s\n",buf);
 
 memset(buf,0,sizeof(buf));  
 strftime(buf, sizeof(buf), "%Y-%m-%d %H:%M:%S", &gm); 
 printf("   gm: %s\n", buf);

 struct tm tm;
 strptime(buf, "%Y-%m-%d %H:%M:%S" , &tm);  
 printf("strptime asctime: %s\n",asctime(&tm)); 

 



 return 0;
}
