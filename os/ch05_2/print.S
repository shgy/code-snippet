; 1. 如果待写入位置超过了屏幕显示的区域, 则向上滚屏
; 2. 如果字符是回车 换行,则需要特殊处理
;
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
