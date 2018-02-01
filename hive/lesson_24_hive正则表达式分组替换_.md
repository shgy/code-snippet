1. 通常, 我们需要分组替换的需求. 比如, 需要比较版本号的大小, 版本号的样例如下: ('v5.12.0', '5.9.0').
'v5.12.0' 是最新版本.  这就需要用到分组替换, Hive SQL如下:
```
 select regexp_replace('5.9.0','v?(\\d+)\\.(\\d)\\.(\\d+)','v$1.0$2.$3')
# v5.09.0
``` 
其实就是添加了前导0, 这样才是正确的字符串比较.

参考: https://stackoverflow.com/questions/28592180/regexp-replace-capturing-groups
