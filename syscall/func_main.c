#include<stdio.h>
/*
 * argv[0]包含了调用程序的名称；利用这一特性可以个实用的小技巧.
 * 首先为同一程序创建多个链接(名称不同), 然后让程序查看argv[0], 根据调用程序的名称来执行不的任务.
 * 典型案例有: gzip/gunzip/zcat; busybox
 * */
int main(int argc, char *argv[])
{
 printf("%s\n", argv[0]);
 return 0;
}
