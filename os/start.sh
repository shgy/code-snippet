#!/bin/bash
set -e
set -x
nasm -o boot/mbr.bin boot/mbr.S
dd if=boot/mbr.bin of=hd60M.img bs=512 count=1 conv=notrunc

nasm -f elf -o boot/print.o boot/print.S
gcc -m32 -c -o boot/kernel.o boot/kernel.c
ld -m elf_i386 -Ttext 0x10000 -e main -o boot/kernel.bin boot/kernel.o boot/print.o
dd if=boot/kernel.bin of=hd60M.img bs=512 count=100 seek=1 conv=notrunc
bochs -f bochsrc.disk
