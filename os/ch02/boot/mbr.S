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


# 获取光标位置
; .get_cursor
mov ah, 3
mov bh, 0
int 0x10

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

