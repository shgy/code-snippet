在python中,时间有三种不同的形态, 分别是

类型 | 函数| 样例
 ----|------|----
字符串| time.ctime() time.asctime()| Fri Apr  8 15:27:19 2016
元组时间(struct_time) | time.localtime() time.gmtime()|time.struct_time(tm_year=2016, tm_mon=4, tm_mday=8, tm_hour=15, tm_min=29, tm_sec=54, tm_wday=4, tm_yday=99, tm_isdst=0)
时间戳 | time.time()| 1460100522.818519

#####1 字符串时间time.ctime()#####
如果用MVC的思想来理解, 字符串格式属于View层, 用于展示.
```
>>> time.ctime()
'Fri Apr  8 17:20:13 2016'
```
上面的格式是如何生成的呢? 看下面的例子.
```
>>> time.strftime('%a %b %-d %X %Y')
'Fri Apr 8 17:28:57 2016'
```

关于时间格式化的描述,可以到官方文档 file:///opt/offical-docs/python-2.7.11-docs-html/library/time.html#time.asctime 中查看. 这里提供部分的指令.

指令| 说明
----|----
 %a | Locale’s abbreviated weekday name.
 %A | Locale’s full weekday name.  
 %b | Locale’s abbreviated month name.     
 %B | Locale’s full month name.    
... | ...



#####2元组类型 time.localtime()/ time.gmtime()#####

如果用MVC的思想来理解, 元组类型属于Controller层, 时间都是分解的, 可以根据业务需求返回不同的时间(其实就是本地时间和格林尼治标准时间).
```
>>> time.localtime()
time.struct_time(tm_year=2016, tm_mon=4, tm_mday=8, tm_hour=20, tm_min=14, tm_sec=13, tm_wday=4, tm_yday=99, tm_isdst=0)
>>> time.gmtime()
time.struct_time(tm_year=2016, tm_mon=4, tm_mday=8, tm_hour=12, tm_min=14, tm_sec=56, tm_wday=4, tm_yday=99, tm_isdst=0)
# 正好相差8个小时
```

#####3 时间戳 time.time()#####
如果用MVC的思想来理解, 时间戳属于Model层.是最原始的时间存储方案.
计算机的计时是从1970年1月1日00:00:00开始, 所以time.time()即是从1970年1月1日00:00:00到当前时间点的秒数.

```
>>> time.time()/(60*60*24*365)
46.299693760168886
# 1970+46 = 2016 
```

为什么计算机的计时是从1970年1月1日00:00:00开始的呢？
以前的Unix计算机中存储时间，是以32位来存储的。因为用32位来表示时间的最大间隔是68年，而最早出现的UNIX操作系统考虑到计算机产生的年代和应用的时限
综合取了1970年1月1日作为UNIX TIME的纪元时间(开始时间)，将1970年作为中间点，向左向右偏移都可以照顾到更早或者更后的时间，因此将1970年1月1日0点作为
计算机表示时间的原点，从1970年1月1日开始经过的秒数存储为一个32位整数。以后计算时间就把这个时间（1970年1月1日00:00:00）当做时间的零点。这种高效简洁的
时间表示法，就被称为"Unix时间纪元"。

#####4 三种时间格式的转换#####

源时间格式 | 目标时间格式 | 转换函数 | 样例
 ----|------|----|----
字符串时间 | 元组时间(struct_time)  |  time.strptime() |  time.strptime('Fri Apr 8 21:16:43 2016')
元组时间(struct_time)  |  字符串时间 |  time.strftime()  time.asctime() | time.strftime('%a %b %d %H:%M:%S %Y')
元组时间(struct_time)  |   时间戳  |    time.mktime()   *   | time.mktime(time.localtime())
时间戳  | 元组时间(struct_time)  | time.localtime() time.gmtime() | time.localtime()
时间戳 | 字符串时间  | time.ctime() | time.ctime()

```
#======================================================================================
>>> time.strptime('Fri Apr 8 21:16:43 2016')
time.struct_time(tm_year=2016, tm_mon=4, tm_mday=8, tm_hour=21, tm_min=16, tm_sec=43, tm_wday=4, tm_yday=99, tm_isdst=-1)
#======================================================================================
>>> time.strftime('%a %b %d %H:%M:%S %Y')
'Sat Apr 09 09:57:49 2016'
#======================================================================================
>>> time.asctime(time.gmtime(0))
'Thu Jan  1 00:00:00 1970'
#======================================================================================
>>> time.mktime(time.localtime())
1460167129.0
#======================================================================================
>>> time.ctime(0)
'Thu Jan  1 08:00:00 1970'
```
