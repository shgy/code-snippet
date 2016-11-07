学习操作系统,参考<操作系统真象还原>一书. 配合Linux三本经典教材: 
LKD3          Linux Kernel Development 3rd
ULK3          Understanding the Linux Kernel 3rd
PLKA          Professional Linux Kernel Architecture

编程,作为为一门技术, 与传统的工匠没有什么差别, 需要大量的实践. 操作系统的学习, 
理解理论, 理解实现的方法, 毕竟隔着一层, 大部分靠的是死记. 而自己动手实践可能理解到的会不一样.
"大师当然有很高技巧，但在成为大师之前，一定是非常好的匠人"
 闲话少述, 进入正题.

按照书中的记录, 配置bochs虚拟机. 为了方便,并没有完全按照书中说明的版本组合: centos-6.3-i386 + bochs-2.6.2
而是使用ubuntu-14.04-x86_64 + bochs-2.6.8的版本组合. 
Step 1: 创建虚拟硬盘, 命令参数与书中描述的有所不同.
```
bximage -hd="60M" -mode="create" -q hd60M.img
```
当然, 也可以使用vboxmanage的命令
```
VBoxManage createmedium disk --filename 100MB_VHD.vhd --size 100 --format VHD --variant Fixed
```
Step 2: 创建配置文件, 注意"keyboard"选项.
```
$ cat bochsrc.disk 
megs: 32
romimage: file=/usr/local/share/bochs/BIOS-bochs-latest
vgaromimage: file=/usr/local/share/bochs/ VGABIOS-lgpl-latest
boot: disk
log: bochs.out
mouse: enabled=0
keyboard: keymap=/usr/local/share/bochs/keymaps/x11-pc-us.map

ata0: enabled=1, ioaddr1=0x1f0, ioaddr2=0x3f0, irq=14
ata0-master: type=disk, path="hd60M.img", mode=flat
```

Step 3:
```
bochs -f bochsrc.disk
```
如果一切正常,最后会提示"No bootable device"

