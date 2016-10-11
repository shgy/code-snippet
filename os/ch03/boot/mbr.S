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
    mov byte [gs:0xb80a0],'P'  
    
	hlt

message db "  1 MBR"

; 定义GDT
gdt_items  dd 0x00000000,0x00000000 ; 空GDT
           dd 0x0000ffff,0x00cf9800 ; 代码段 0x0000 4G
           dd 0x0000ffff,0x00cf9200 ; 数据段 0x0000 4G
gdt_size dw 0x0017
gdt_base dd gdt_items

times 510-($-$$) db 0
dw 0xaa55

