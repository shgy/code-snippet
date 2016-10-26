cd initramfs
find . | cpio -o -H newc | gzip -9 > ../initrd.img 
