在lesson_05的实现中,为了简化问题, 直接在kernel.c中嵌入了汇编代码.这只是为了演示功能, 简单粗暴.
为了更灵活地实现打印的功能, 本节将对代码进行改造:
1. 添加保护模式的栈段: 前面只有平坦模式的代码段和数据段, 是因为都没有用到栈.
参考<x86汇编语言-从实模式到保护模式>一书, 使用0x7a00~0x7c00共512字节的空间.
```
; 定义GDT
gdt_items  dd 0x00000000,0x00000000 ; 空GDT
           dd 0x0000ffff,0x00cf9800 ; 代码段 0x0000 4G
           dd 0x0000ffff,0x00cf9200 ; 数据段 0x0000 4G
           dd 0x00007a00,0x00409600 ; 栈段
```
使用如下的代码初始化栈:
```
    ; 初始化栈段
    mov cx,0x18 ;
    mov ss,cx
    mov esp,0x7c00
    mov ebp,esp
```
使用如下的代码测试栈的使用, 要用到`print-stack`指令
```
; test push
mov ax, 1
push ax
pop bx
```
2. 使用汇编和C混合, 实现字符的打印功能.

2.1 定义数据类型 stdint.h
```
#ifndef __LIB_STDINT_H
#define __LIB_STDINT_H
typedef signed char int8_t;
typedef signed short int int16_t;
typedef signed int int32_t;
typedef signed long long int int64_t;
typedef unsigned char uint8_t;
typedef unsigned short int uint16_t;
typedef unsigned int uint32_t;
typedef unsigned long long int uint64_t;
#endif
```
2.2 定义函数头 print.h
```
#ifndef __LIB_KERNEL_PRINT_H
#define __LIB_KERNEL_PRINT_H
#include "stdint.h"
void put_char(uint8_t char_asci);
#endif
```

2.3 使用汇编实现print.h中定义的函数
```
[bits 32]
section .text
global put_char

put_char:
   pushad

   ; 获取当前光标 高8位
   mov dx, 0x03d4
   mov al, 0x0e
   out dx, al
   mov dx, 0x03d5
   in al, dx

   mov ah, al
   ; 获取当前光标 低8位
   mov dx, 0x03d4
   mov al, 0x0f
   out dx, al
   mov dx, 0x03d5
   in al, dx

   xor ebx, ebx ; 防止高16位的数据影响, 因为会用到ebx
   mov bx, ax

   ; 从栈中取出参数
   mov cl, [esp + 36]

   ; 是否是回车符
   cmp cl, 0x0d
   jz .carrage_return

   ; 是否是换行符
   cmp cl, 0x0a
   jz .line_feed

   shl bx,1 ; 光标位置*2 才代表真正写入地址, 因为每个字符需要2个字节表示
   mov byte [gs: 0xb8000 + ebx],cl
   shr bx,1 ;
   add bx, 1 ; 光标右移一位

   ; 是否需要滚屏
   cmp bx, 2000 ; 25 * 80
   jl .set_cursor

 ; 将换行回车都置到下一行的行首
 .carrage_return:
 .line_feed:
    xor dx, dx
    mov ax, bx
    mov si, 80
    div si   ; 16位的除数；商在ax中, 余数在dx中
    sub bx, dx ; 到行首
    add bx, 80 ; 到下一行
    cmp bx, 2000
    jl .set_cursor

 .roll_screen:
    cld ; cld是将方向标志位DF设置为0，每次rep循环的时候,esi和edi自动+1。   std是将方向标志位DF设置为1，每次rep循环的时候,esi和edi自动-1。
    mov ecx, 960 ; (2000-80)*2/4=960
    mov esi, 0xb80a0
    mov edi, 0xb8000
    rep movsd
    ;将最后一行以空白填充
    mov ebx, 3840 ; 3840 + 160 = 4000
    mov ecx, 80

   .cls:
    mov word [gs: 0xb8000 + ebx], 0x0720 ; 0x0720 黑底白字的空格键
    add ebx, 2
    loop .cls
    mov bx, 1920 ; 将光标置于最后一行的行首

 .set_cursor:

   ; 设置光标 高8位
   mov dx, 0x03d4
   mov al, 0x0e
   out dx, al

   mov dx, 0x03d5
   mov al, bh
   out dx, al

   ; 设置光标 低8位
   mov dx, 0x03d4
   mov al, 0x0f
   out dx, al

   mov dx, 0x03d5
   mov al, bl
   out dx, al

   popad
   ret
```

2.4 在kernel.c中调用汇编实现的函数
```
#include"print.h"

int main(int argc, char* argv[])
{
    // __asm__("movb $0x45, %gs:0x000b80a6");
    put_char('P');
    while(1);
}
```

2.5 编译,并写入到虚拟硬盘中
```
nasm -f elf -o boot/print.o boot/print.S
gcc -m32 -c -o boot/kernel.o boot/kernel.c
ld -m elf_i386 -Ttext 0x10000 -e main -o boot/kernel.bin boot/kernel.o boot/print.o

dd if=boot/kernel.bin of=hd60M.img bs=512 count=100 seek=1 conv=notrunc
xxd -u -a -g 1 -c 512 -l 512 hd60M.img
```
