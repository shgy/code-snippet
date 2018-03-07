作者：一绊
链接：https://www.zhihu.com/question/20117388/answer/62263969
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

服务器是ubuntu，用Mac的iterm2 ssh连上去，终端显示中文乱码，也不能输入中文，然而本地终端可以显示和输入。解决方法：这种情况一般是终端和服务器的字符集不匹配，MacOSX下默认的是utf8字符集。输入locale可以查看字符编码设置情况，而我的对应值是空的。因为我在本地和服务器都用zsh替代了bash，而且使用了oh-my-zsh，而默认的.zshrc没有设置为utf-8编码，所以本地和服务器端都要在.zshrc设置，步骤如下，bash对应.bash_profile或.bashrc文件。

1.在终端下输入vim ~/.zshrc
或者使用其他你喜欢的编辑器编辑~/.zshrc文件<!--more-->

2.在文件内容末端添加：
```
export LC_ALL=en_US.UTF-8  
export LANG=en_US.UTF-8
```

接着重启一下终端，或者输入source ~/.zshrc使设置生效。设置成功的话，在本地和登录到服务器输入locale回车会显示下面内容。

```
LANG="en_US.UTF-8"
LC_COLLATE="en_US.UTF-8"
LC_CTYPE="en_US.UTF-8"
LC_MESSAGES="en_US.UTF-8"
LC_MONETARY="en_US.UTF-8"
LC_NUMERIC="en_US.UTF-8"
LC_TIME="en_US.UTF-8"
LC_ALL="en_US.UTF-8"
```
这时，中文输入和显示都正常了。
