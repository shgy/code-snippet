expect命令用于自动实现一些交互式的功能.比如ssh 远程登录；ftp下载文件.

需求1: 通过ssh 命令从远程服务器下载文件

由于expect的spawn不支持管道,因此需要分两步走:
step1 创建download.sh脚本
```
#!/usr/bin/env bash
ssh user@localhost 'cat /var/logs/data.log' > bb.txt
```
step2 创建auto_interaction.sh
```
#!/usr/bin/expect -f
spawn sh download.sh
expect "password:"
send "passwd_of_user\r"
interact
```
step3 执行脚本即可.
```
chmod +x auto_interaction.sh
./auto_interaction.sh
```

这样就实现了上面的需求, 但是它有一个问题, 如果要再下载一个其它的文件,就需要修改脚本了. 不够灵活.
OK, 我们开始修改脚本,使灵动起来.

step1 修改download.sh脚本, 使它可以传递参数
```
#!/usr/bin/env bash
if [ $# != 1 ] ; then
    echo "USAGE: $0 filename"
    exit 1;
fi
ssh shgy@localhost "cat /home/shgy/tmp/$1" > $1
```

step 2 修改auto_interaction.sh, 使它可以传递参数
```
#!/usr/bin/expect -f
set filename [lindex $argv 0]
spawn sh download.sh $filename
expect "password:"
send "passwd_of_user\r"
interact
```
step3 执行脚本
```
./auto_interaction.sh file
```

