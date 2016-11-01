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
	mov ds, ax
    mov byte [gs:0xb80a0],'P'  
    
	; 内存分页: 2级页表 页表的起始地址从0x100000开始
    ; 1 清空1M空间上的第一个4K空间, 作为页目录地址
	
	mov ecx, 4096
	mov esi, 0
	.clear_page_dir:
	   mov byte[0x100000 + esi], 0
	   inc esi
	   loop .clear_page_dir
	
	; 2 创建页目录项(PDE), 只创建一个页目录项就够了. 因为一个页目录项有1024个页表项,即管理了4M内存
	mov eax, 0x101000
	mov ebx, eax
	or eax, 0x07
	mov [0x100000], eax
    
	; 3 创建页表项(PTE), 由于只操作1M内存, 因此4K*256个页表项就够了
	mov ecx, 256
	mov esi, 0
	mov edx, 0x07
	.create_pte:
	  mov [ebx +esi*4], edx
	  add edx, 4096
	  inc esi
	  loop .create_pte
   
   ; 把页目录首地址赋给cr3
   mov eax, 0x100000
   mov cr3, eax
   ; 打开cr0的pg位
   mov eax, cr0
   or eax, 0x80000000
   mov cr0, eax

   mov byte [gs: 0xb80a2], 'K'
  
	hlt

message db "  1 MBR"

; 定义GDT
gdt_items  dd 0x00000000,0x00000000 ; 空GDT
           dd 0x0000ffff,0x00cf9800 ; 代码段 0x0000 4G
           dd 0x0000ffff,0x00cf9200 ; 数据段 0x0000 1M 
gdt_size dw 0x0017
gdt_base dd gdt_items

times 510-($-$$) db 0
dw 0xaa55

