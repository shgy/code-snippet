# -*- coding: utf-8 -*-
# Created by 'shgy' on '15-8-22'

# eval
x = 2
square = eval("x*x")
print square


#  format 函数
"""
format用于 对 数值类型 进行 格式化 成字符串.
"""

"""
1.  整数(二进制, 八进制, 十进制, 十六进制) 转换成 二进制, 八进制, 十进制, 十六进制 字符串
可以使用bin(), 但是bin()会在前面生成 '0b'；

>>> format(10,'b')
'1010'
>>> format(012,'b')
'1010'
>>> format(012,'x')
'a'
>>> format(012,'d')
'10'
>>> format(012,'o')
'12'

如果需要进制标识 '0b', '0o', or '0x'
 format(5,'#b')
'0b101'

如果需要填充字符, 需要指定align
>>> format(5,'0>32b')
'00000000000000000000000000000101'

如果需要每3位一个, 分开
>>> format(1000,',')
'1,000'



2. 整数转换成 ascii码字符
>>> format(48,'c')
'0'
>>> format(65,'c')
'A'
>>> format(97,'c')
'a'


3.  将浮点数转换成 百分比
>>> format(0.56,'.2%')
'56.00%'

4. 格式化时间
>>> import datetime
>>> d = datetime.datetime(2010, 7, 4, 12, 15, 58)
>>> format(d,'%Y-%m-%d %H:%M:%S')
'2010-07-04 12:15:58'

format 函数可以使用 "{:0^5d}".format(10)  来代替. 换句话说, format() 是 string.format()功能的子集

"""