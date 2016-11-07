/*
nasm -f elf -o boot/print.o boot/print.S
gcc -m32 -c -o boot/kernel.o boot/kernel.c
ld -m elf_i386 -Ttext 0x10000 -e main -o boot/kernel.bin boot/kernel.o boot/print.o

dd if=boot/kernel.bin of=hd60M.img bs=512 count=100 seek=1 conv=notrunc
xxd -u -a -g 1 -c 512 -l 512 hd60M.img
*/
#include"print.h"

int main(int argc, char* argv[])
{
    // __asm__("movb $0x45, %gs:0x000b80a6");
    // put_char('P');
    int i;
    for(i=0;i<1000;i++)
        put_char('P');
    put_char('\n');
    for(i=0;i<1000;i++)
        put_char('N');
    while(1);
}