1. iptables由Netfilter项目开发, 自linux-2.4开始, 成为内核的一部分.
2. Netfilter提供了一个内核框架, iptables在它之上建立了防火墙功能.
3. iptables一共有4个表: filter, nat, mangle, raw. 通常用到的是filter表．
4. iptables只过滤IP及其之上协议的数据包, 例如iptables不能过滤ping命令, 因为ping命令采用ARP协议.
