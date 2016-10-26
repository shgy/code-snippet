8086 CPU, 地址线只有20根, 只能寻址1M;
80386 CPU, 地址线有32根, 能寻址4GB.
80386如何兼容8086呢?

解决方案:引入保护模式.

CPU默认工作在实模式下, 需要3步进入保护模式:

Step 1: 使用lgdt指令加载GDT(全局描述符表)<br>
Step 2: 打开A20地址线.<br>
Step 3: 设置保护模式的开关cr0的PE位为1.<br>


GDT可以存储在内存的任意位置, 
lgdt指令的参数也是一个内存位置, 共48位,低16位表示GDT的大小, 高32位表示GDT的起始位置.

GDT中每一项需要64位,即8 byte来描述一个段.
GDT的第一项全0, 相当于NULL.
相关的代码如下:
```
SECTION mbr align=16 vstart=0x7c00

mov ax, 0
mov ds, ax
mov es, ax

;# clear screen
mov ax, 0x600 
mov bx, 0x700
mov cx, 0
mov dx, 0x184f

int 0x10


; # 显示字符串
mov ax, message
mov bp, ax
mov cx, 7
mov ax, 0x1301

mov bx, 0x2
int 0x10

; protect mode step 1
lgdt [gdt_size]
; protect mode step 2
in al,0x92                         ;南桥芯片内的端口 
or al,0000_0010B
out 0x92,al                        ;打开A20

cli
; protect mode step 3
mov eax,cr0
or eax,1
mov cr0,eax                        ;设置PE位

; 进入保护模式
jmp dword 0x0008: flush  ; 由于代码段起始地址是0x7c00, 而vstart=0x7c00, 所以要减去0x7c00

[bits 32]
flush:
	mov ax, 0x0010
	mov gs, ax
    mov byte [gs:0xb80a0],'P'  ; 保护模式下偏移量是32位
    
	hlt

message db "  1 MBR"

; 定义GDT
gdt_items  dd 0x00000000,0x00000000 ; 空GDT
           dd 0x0000ffff,0x00cf9800 ; 代码段 0x0000 4G
           dd 0x0000ffff,0x004f9200 ; 数据段 0x0000 1M 
gdt_size dw 0x001f
gdt_base dd gdt_items

times 510-($-$$) db 0
dw 0xaa55
```


