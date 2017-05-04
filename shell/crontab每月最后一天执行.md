```
0 8 28-31 * * [ `date -d tomorrow +%e` -eq 1 ] && (shell script)
```
原理: 每月的最后3天每天执行命令:
```
[ `date -d tomorrow +%e` -eq 1 ] && (shell script)
```
如果[ `date -d tomorrow +%e` -eq 1 ] 返回false, 则 ( shell script ) 不执行.

参考:
http://www.codesky.net/article/201109/133201.html
