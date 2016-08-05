#include<stdio.h>
#include<string.h>
#include<stdlib.h>
#include<errno.h>
/*
  注意的是 在ubuntu系统中, rename命令是一个perl脚本.
  因此其用法有些不同: 
  比如 当前目录中有foo1, foo2, foo3, foo4 ... 以foo为前缀的文件, 需要将前缀修改为foox
  命令如下: rename 's/foo/foox/' foo*

  如果 将文件名aa 修改为文件名 bb, 则使用 mv命令即可.使用rename aa bb 会报错:
  Bareword "aa" not allowed while "strict subs" in use at (eval 1) line 1.
  
  对于rename()函数:  rename(oldpath, newpath)
  若newpath已经存在, 则将其覆盖.
  与unlink()函数一样, oldpath, newpath指代的文件必须位于同一文件系统
 */
int main(int argc, char *argv[])
{
 if( argc != 3 )
 {
   printf("Usage %s oldpath newpath\n", argv[0]);
   exit(1);
 }

 if( rename(argv[1], argv[2]) == -1 ){
   printf("Rename Error: %s\n", strerror(errno));
   exit(1);
 }

 printf("Rename Success\n");
 return 0;
}


