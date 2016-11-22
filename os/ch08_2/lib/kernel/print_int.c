#include "print.h"

static void int_to_hex(char* output, uint32_t src);


static void int_to_hex(char* output, uint32_t src)
{
    output[0]='0';
    output[1]='x';
    uint8_t idx = 9, left = 0;

    while(src > 0)
    {
      left = src % 16;
      left = left < 10 ? left + 48 : left + 87 ;
      output[idx--] = left ;
      src = src >> 4;
    }
    for(;idx>1;idx--)output[idx] = '0';
    output[10] = 0;
}


void put_int(uint32_t num)
{
   char output[11];
   int_to_hex(output, num);
   put_str(output);
}