#!/usr/bin/env bash

nasm -o build/mbr.bin boot/mbr.S


gcc  -m32 -I lib/kernel/ -I lib/ -I kernel/ -c -fno-builtin -o build/main.o kernel/main.c

nasm -f elf -o build/print.o lib/kernel/print.S
nasm -f elf -o build/kernel.o kernel/kernel.S

# 添加 -fno-stack-protector  是为了避免   undefined reference to `__stack_chk_fail'
gcc -m32 -I lib/kernel/ -I lib/ -I kernel/ -c -fno-stack-protector -o build/print_int.o lib/kernel/print_int.c
gcc -m32 -I lib/kernel/ -I lib/ -I kernel/ -c -fno-stack-protector -o build/interrupt.o kernel/interrupt.c
gcc -m32 -I lib/kernel/ -I lib/ -I kernel/ -c -fno-stack-protector -o build/init.o kernel/init.c

ld -m elf_i386 -Ttext 0x10000 -e main -o build/kernel.bin \
   build/main.o build/init.o build/interrupt.o build/print.o build/kernel.o build/print_int.o