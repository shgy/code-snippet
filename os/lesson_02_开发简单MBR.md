在lesson1中, 已经搭建好了基础环境, 但是在运行时出现了"No bootable device"的提示. 
即创建的虚拟硬盘hd60M.img不是一个可启动的硬盘.我们需要往硬盘中写一些东西, 让系统识别它是可启动的.
即在硬盘的0盘0道1扇区中写入我们的执行代码, 如下:
```
SECTION mbr align=16 vstart=0x7c00

mov ax, cs
mov ds, ax
mov es, ax
mov ss, ax
mov fs, ax
mov sp, 0x7c00

# clear screen
mov ax, 0x600 
mov bx, 0x700
mov cx, 0
mov dx, 0x184f

int 0x10


# get cursor position
mov ah, 3
mov bh, 0
int 0x10

# print message
mov ax, message
mov bp, ax
mov cx, 5
mov ax, 0x1301

mov bx, 0x2
int 0x10

jmp $
message db "1 MBR"
times 510-($-$$) db 0
dw 0xaa55

```
3个功能点,全部用的是BIOS的0x10号中断. 当然也可以自己直接控制显存.
