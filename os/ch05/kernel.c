/*
gcc -m32 -c -o kernel.o kernel.c
ld -m elf_i386 boot/kernel.o -Ttext 0x10000 -e main -o boot/kernel.bin

dd if=boot/kernel.bin of=hd60M.img bs=512 count=100 seek=1 conv=notrunc
xxd -u -a -g 1 -c 512 -l 512 hd60M.img
*/
int main(int argc, char* argv[])
{
    __asm__("movb $0x45, %gs:0x000b80a6");
    while(1);
}