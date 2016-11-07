#!/usr/bin/env bash

nasm -o build/mbr.bin boot/mbr.S


gcc  -m32 -I lib/kernel/ -I lib/ -I kernel/ -c -fno-builtin -o build/main.o kernel/main.c

nasm -f elf -o build/print.o lib/kernel/print.S
nasm -f elf -o build/kernel.o kernel/kernel.S

gcc -m32 -I lib/kernel/ -I lib/ -I kernel/ -c -fno-builtin -o build/interrupt.o kernel/interrupt.c
gcc -m32 -I lib/kernel/ -I lib/ -I kernel/ -c -fno-builtin -o build/init.o kernel/init.c

ld -m elf_i386 -Ttext 0x10000 -e main -o build/kernel.bin build/main.o build/init.o build/interrupt.o build/print.o build/kernel.o