#!/bin/bash
set -e
set -x

cd ch07_3 
sh build.sh
dd if=build/mbr.bin  of=../hd60M.img bs=512 count=1 conv=notrunc
dd if=build/kernel.bin of=../hd60M.img bs=512 count=100 seek=1 conv=notrunc
cd ../
bochs -f bochsrc.disk
