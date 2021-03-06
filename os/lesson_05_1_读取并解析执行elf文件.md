参考图书<Orange's: 一个操作系统的实现>第5章.

由于使用的操作系统是64位的, 而书中描述的是32位的做法. 解决方法就是给64位的系统开启32位的支持.
```
sudo dpkg --add-architecture i386
sudo apt-get update
sudo apt-get install libc6:i386 libncurses5:i386 libstdc++6:i386
```

然后在编译程序的时候,都使用32位即可
```
gcc -g -c -m32 -o bar.o bar.c
nasm -f elf -o foo.o foo.asm
ld -m elf_i386 -o foobar foo.o bar.o
./foobar
```

由于写myprint汇编代码,少写了ret指令, 导致代码死循环. 使用gdb进行了调试,参考http://www.linuxidc.com/Linux/2014-10/108574.htm

与Java的class文件一样, elf文件也是一个表格.
elf给每个段都进行了定位, 程序加载到内存中时, 将段加载到指定的位置即可. 因此ELF文件格式的处理过程如下:
1. 将elf文件加载到内存.
2. 解析elf文件的Program Header中的地址,并将每个Proram Header代表的数据copy到指定的内存位置.
3. 跳转到elf文件的入口地址.

操作系统启动的过程：
引导　-->　加载内核　--> 解析内核／设置保护模式／分页　-->　启动内核


由于elf是32位代码, 因此其执行只能在保护模式下.
由于只是展示如何解析elf并执行文件, 因此不必考虑代码的通用性.
低1M内存空间中0x7e00到0x9fbff中一共有608K可用空间

目前的做法如下:
1. 开发一个elf格式的可执行文件, 链接时将执行入口定位到50000
2. 将elf格式的可执行文件写入到硬盘的第2扇区
3. 在实模式下读取硬盘的第2扇区到内存0x7e00,并解析elf文件,将相应的段copy到指定的位置
4. 在保护模式下执行elf的代码



关于elf格式:
在32位的平台上,前52个字节是ELF Header; 接下来就是Program Header, 每个Program Header占32个字节
首先, 从ELF Header中得到Number of program headers, 即e_phnum字段的值
然后从每个Program Header中拿到: 偏移量p_offset, 虚拟内存位置 p_vaddr, p_filesize复制的字节数

由于事先已经知道只有一个Program Header, 为了简化程序, 只复制一个Program Header即可.
p_offset在文件的第56个字节的位置,
p_vaddr在文件的第60个字节的位置,
p_filesz在文件的第68个字节的位置

参考: AT&T与Intel格式的汇编语法(http://blog.csdn.net/lincyang/article/details/35321687)
由于已知gs中存放的是DATA段的段描述符, 因此使用内联汇编代码:
```
int main(int argc, char* argv[])
{
    __asm__("movb $0x45, %gs:0x000b80a6");
    while(1);
}
```
直接写一个字符"E"到显存中.
编译, 并写入硬盘
```
gcc -m32 -c -o kernel.o kernel.c
ld -m elf_i386 boot/kernel.o -Ttext 0x10000 -e main -o boot/kernel.bin

dd if=boot/kernel.bin of=hd60M.img bs=512 count=100 seek=1 conv=notrunc
xxd -u -a -g 1 -c 512 -l 512 hd60M.img
```
这里, 将elf可执行文件的入口设置为0x10000, 是因为在低1M的内存空间中, 有608K的地址是可用的.


16 0x00: e_ident
2  0x10: e_type
2  0x12: e_machine
4  0x14: e_version
4  0x18: e_entry
4  0x1c: e_phoff
4  0x20: e_shoff
4  0x24: e_flags
2  0x28: e_ehsize
2  0x2a: e_phentsize
2  0x2c: e_phnum
2  0x2e: e_shentsize
2  0x30: e_shnum
2  0x32: e_shstmdx

--- 2016-11-06 补充
在学习第7章 中断时, 发现kernel.bin的elf是两个段, 因此在ch-07中对此进行了修改.



