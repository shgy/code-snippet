cd命令是shell的内置命令, 因此使用`man cd`是找不到cd命令的帮助文档的. 要使用`man bash`才行.
cd的说明文档如下:
```
cd [-L|[-P [-e]] [-@]] [dir]
              Change the current directory to dir.  if dir is not supplied, the value of the HOME shell variable is the default.  Any additional arguments  following  dir  are  ignored.   The  variable
              CDPATH defines the search path for the directory containing dir: each directory name in CDPATH is searched for dir.  Alternative directory names in CDPATH are separated by a colon (:).  A
              null directory name in CDPATH is the same as the current directory, i.e., ``.''.  If dir begins with a slash (/), then CDPATH is not used. The -P option causes  cd  to  use  the  physical
              directory structure by resolving symbolic links while traversing dir and before processing instances of .. in dir (see also the -P option to the set builtin command); the -L option forces
              symbolic links to be followed by resolving the link after processing instances of .. in dir.  If .. appears in dir, it is processed by removing the immediately previous pathname component
              from  dir, back to a slash or the beginning of dir.  If the -e option is supplied with -P, and the current working directory cannot be successfully determined after a successful directory
              change, cd will return an unsuccessful status.  On systems that support it, the -@ option presents the extended attributes associated with a file as a directory.  An argument of - is con‐
              verted  to $OLDPWD before the directory change is attempted.  If a non-empty directory name from CDPATH is used, or if - is the first argument, and the directory change is successful, the
              absolute pathname of the new working directory is written to the standard output.  The return value is true if the directory was successfully changed; false otherwise.

```
cd命令的使用很简单, 切换工作目录(change directory). 我们关注的是其参数
`-P`参数: 表示显示真实的地址, 用于目录中有软链接的情况.
```
shgy@shgy-thinkpad:~$ cd baiduyun
shgy@shgy-thinkpad:~/baiduyun$ cd ..
shgy@shgy-thinkpad:~$ cd -P baiduyun
shgy@shgy-thinkpad:/data1/linux-win-share/百度云同步盘$ 
```