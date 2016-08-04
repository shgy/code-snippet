#include<stdio.h>
/**
 *
 * stdout 输出到交互式环境是行缓冲, 输出到文件是全缓冲.
 * stderr 没有缓冲.
 * 因此, 输出结果为 World!Hello 
 */
int main(int argc, char *argv[])
{

 printf("FD of stdin is: %d\n", stdin->_fileno);
 printf("FD of stdout is: %d\n", stdout->_fileno);
 printf("FD of stderr is: %d\n", stderr->_fileno);

 fprintf(stdout, "%s", "Hello ");
 fprintf(stderr, "%s", "World!");

 return 0;
}

