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
    ; 初始化栈段
    mov cx,0x18 ;
    mov ss,cx
    mov esp,0x7c00
    mov ebp,esp
	mov ax, 0x0010
	mov gs, ax

    ; 设置读取硬盘扇区数
    mov dx,0x1f2
    mov al, 0x64 ; 读取100个扇区
    out dx, al
    ; 设置起始的扇区号 1
    mov dx, 0x1f3  ;0x1f3 0~7
    mov al, 0x01
    out dx, al
    inc dx         ;0x1f4 8~15
    mov al, 0x00
    out dx, al
    inc dx         ;0x1f5 16~23
    out dx, al
    inc dx         ;0x1f6 24~27
    mov al, 0xe0
    out dx, al
    ; 发出读请求
    mov dx, 0x1f7
    mov al, 0x20
    out dx, al
    ; 等待 接收数据
    mov dx, 0x1f7
    .waits:
        in al, dx
        and al, 0x88
        cmp al, 0x08
        jnz .waits
    ; 读取数据到内存中: 每次读取2个字节, 100个扇区一共: 100*512/2 = 25600 = 0x6400次
    mov ecx, 0x6400
    mov dx, 0x1f0
    mov ebx, 0x7e00
    .read:
       in ax,dx
       mov [gs:ebx], ax
       add ebx, 2
       loop .read

    ; 由于 movsb指令会使用ds和es寄存器, 因此在保护模式下, 将gs中的data段赋给ds和es
    mov ax, gs
    mov ds, ax
    mov es, ax
    xor ebx, ebx
    xor ecx, ecx
    xor edx, edx
    ; 解析ELF Header,
    mov word bx, [gs: 0x7e00+0x28] ;首段的偏移
    mov word dx, [gs: 0x7e00+0x2a] ;得到段的大小
    mov word cx, [gs: 0x7e00+0x2c] ;得到段的个数
    dec ecx
    .parse_ph:

       call .copy_segment
       add ebx, edx
       loop .parse_ph

    ; 跳转到elf代码的位置
    jmp dword 0x0008: 0x10000

    .copy_segment:
        push ecx
        push edx
        ; 解析Program Header
        mov eax, [gs: 0x7e00+ebx+0x04]
        mov edx, [gs: 0x7e00+ebx+0x08] ; 段的虚拟地址
        mov ecx, [gs: 0x7e00+ebx+0x14] ; 段的大小

        mov edi, edx	   ; dst
        add eax, 0x7e00
        mov esi, eax	   ; src
        rep movsb		   ; 逐字节拷贝

        pop edx
        pop ecx
        ret

	hlt

; 定义GDT
gdt_items  dd 0x00000000,0x00000000 ; 空GDT
           dd 0x0000ffff,0x00cf9800 ; 代码段 0x0000 4G
           dd 0x0000ffff,0x00cf9200 ; 数据段 0x0000 4G
           dd 0x00007a00,0x00409600 ; 栈段
gdt_size dw 0x001f
gdt_base dd gdt_items

times 510-($-$$) db 0
dw 0xaa55

