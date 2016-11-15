在ubuntu系统中, 直接从图形界面双击启动和从shell中启动eclipse, 会有所不同.直接从图形界面中启动, 对/etc/profile变量的变化无法感知.
需要系统re-login才行.
这与几个文件有关: /etc/profile  ~/.profile  /etc/bash.bashrc ~/.bashrc
参考: https://help.ubuntu.com/community/EnvironmentVariables#Desktop_environment_specifics

最后eclipse的启动脚本如下:
```
#!/bin/bash
bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
nohup "${bin}"/eclipse > /dev/null 2>&1 &
```
