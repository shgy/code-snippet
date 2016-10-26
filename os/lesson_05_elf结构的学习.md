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



