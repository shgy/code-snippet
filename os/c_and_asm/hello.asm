[section .data]
strHello db "hello, world!", 0AH
strlen equ $ - strHello

[section .text]
global _start

_start:
  mov edx, strlen
  mov ecx, strHello
  mov ebx, 1
  mov eax, 4
  int 0x80
  mov ebx, 0
  mov eax, 1
  int 0x80
