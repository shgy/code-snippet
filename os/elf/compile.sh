gcc -c -m32 -o bar.o bar.c
nasm -f elf -o foo.o foo.asm
ld -m elf_i386 -o foobar foo.o bar.o
./foobar
