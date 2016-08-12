expect命令用于自动实现一些交互式的功能.比如ssh 远程登录；ftp下载文件.

`如果一些机器上没有scp命令, 可以用ssh + cat实现文件的下载功能.`

需求1: 通过ssh 命令从远程服务器下载文件

由于expect的spawn不支持管道,因此需要分两步走:
step1 创建download.sh脚本
```
#!/usr/bin/env bash
ssh user@localhost 'cat /var/logs/data.log' > bb.txt
```
step2 创建auto_download.sh
```
#!/usr/bin/expect -f
spawn sh download.sh
expect "password:"
send "passwd_of_user\r"
interact
```
step3 执行脚本即可.
```
expect -f auto_interaction.sh
```

这样就实现了上面的需求, 但是它有一个问题, 如果要再下载一个其它的文件,就需要修改脚本了. 不够灵活.
OK, 我们开始修改脚本,使它灵动起来.

___

step1 修改download.sh脚本, 使它可以传递参数
```
#!/usr/bin/env bash
if [ $# != 1 ] ; then
    echo "USAGE: $0 filename"
    exit 1;
fi
ssh shgy@localhost "cat /var/logs/$1" > $1
```

step 2 修改auto_download.sh, 使它可以传递参数. 需要注意的是, expect的参数传递与shell的参数传递方式不一样.
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
expect -f auto_download.sh file
```

接下来, 如果需要批量下载怎么办呢?
```
#!/usr/bin/env bash

for i in `seq 1 60`
do
  expect -f auto_download.sh "data.$i"
done
```

最后总结一下吧!
这里涉及到的比较重要的点在于:
1. 使用ssh+cat实现scp的功能, 属于"知其道, 用其妙"的境界.
2. expect命令的download不支持管道, 需要绕过.
3. expect脚本不能使用sh xxx.sh, 而要使用expect -f xxx.sh (这个是容易踩坑的点)






