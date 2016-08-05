#define _XOPEN_SOURCE
#include<stdio.h>
#include<unistd.h>
#include<stdlib.h>
#include<string.h>
#include<errno.h>
/*
 * $ gcc func_crypt.c -lcrypt # 需要链接crypt库
 * 
 * */
int main(int argc, char *argv[])
{
    if(argc != 3)
    {
        fprintf(stderr, "%s salt key\n", argv[0]);
        return 1;
    }
    char *encrypted = NULL;

    if((encrypted = crypt(argv[2], argv[1])) == NULL)
    {
        fprintf(stderr, "crypt error:%s\n", strerror(errno));
    }

    printf("%s encrypted:%s\n", argv[2], encrypted);

    return 0;
}
