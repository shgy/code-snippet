#include<stdio.h>
typedef unsigned int uint32_t;
typedef unsigned char uint8_t;
// 将16进制整型转换成字符串: 0x00000011
void int_to_hex(char* output, uint32_t src)
{
    output[0]='0';
    output[1]='x';
    uint8_t idx = 9, left = 0, val=0;

    while(src > 0)
    {
      left = src % 16;
      left = left < 10 ? left + 48 : left + 87 ;
      output[idx--] = left ;
      src = src >> 4;
    }
    for(;idx>1;idx--)output[idx] = '0';
}

int main(int argc, char* argv[])
{
  char output[11]={0};
  int_to_hex(output, 0x20);
  printf("output= %s\n",output);
  return 0;
}
