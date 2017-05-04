Java的平台无关,最基础的一点就是基础类型的表示范围.


浮点数.

比如, 计算`2.0 - 1.1`
```
	public static void main(String[] args) {
		System.out.println(2.0-1.1); // 0.8999999999999999

	}
```
浮点数采用 `IEEE 754`规范.

对于无理数,例如: PI, 1/3等, 计算机无法精确的直接存储. 因此, 计算会有误差. 这对于普通的工程应用, 没有问题. 但是对精度要求高的金融类应用,
就有心无力了. 得使用`BigDecimal`
```
	public static void main(String[] args) {
		System.out.println(2.0-1.1); // 0.8999999999999999
		System.out.println( BigDecimal.valueOf(2.0).subtract( BigDecimal.valueOf(1.1)).doubleValue());// 0.9
	}
```

char.

原本以为Java中, char类型很简单,就是表示一个字符. 看<Java Core>才发现,不是这回事.



世界上本没有字符编码。自从有了计算机，我们有了用0和1记录文字的需求，于是字符就有了编码。

最开始, 采用的编码是`ASC-II`, 8位一个字节存储. 缺陷是, 只能表示英文. 对于西欧, 中文等就无能为力.
于是, 各国开始设计能够处理各国文字的编码, 比如中国的GBK, 西欧的ISO-8859-1, 日本的Shift_JIS ...
这些编码都兼容ASC-II, 但是如果一段文本既包含中文, 又包含日文, 处理起来就麻烦了
于是, 在20世纪80年代, 编码界的大师们觉得可行的办法就是"车同轨、书同文". 就开始了Unicode编码的设计.
当时认为两个字节足够对世界所有的字符进行编码. 这就是Unicode的基本字符(BMP).
后来当然就不够用了. 怎么办呢? 继续往后加呗. 这就是 所谓的 增补字符 (Supplementary Code Point)



length()函数返回采用UTF-16编码标识的给定字符串所需要的代码单元的数量。
codePointCount()函数返回采用UTF-16编码标识的给定字符串所需要的代码点的数量。

.


UCS规定了怎么用多个字节表示各种文字。怎样传输这些编码，是由UTF(UCS Transformation Format)规范规定的，
常见的UTF规范包括UTF-8、UTF-7、UTF-16。

IETF的RFC2781和RFC3629以RFC的一贯风格，清晰、明快又不失严谨地描述了UTF-16和UTF-8的编码方法。
我总是记不得IETF是Internet Engineering Task Force的缩写。但IETF负责维护的RFC是Internet上一切规范的基础。

3、UCS-2、UCS-4、BMP

UCS有两种格式：UCS-2和UCS-4。顾名思义，UCS-2就是用两个字节编码，UCS-4就是用4个字节（实际上只用了31位，最高位必须为0）
编码。下面让我们做一些简单的数学游戏：
UCS-4根据最高位为0的最高字节分成2^7=128个group。每个group再根据次高字节分为256个plane。每个plane根据第3个字节分为256行 (rows)，每行包含256个cells。当然同一行的cells只是最后一个字节不同，其余都相同。

group 0的plane 0被称作Basic Multilingual Plane, 即BMP。或者说UCS-4中，高两个字节为0的码位被称作BMP。

将UCS-4的BMP去掉前面的两个零字节就得到了UCS-2。在UCS-2的两个字节前加上两个零字节，就得到了UCS-4的BMP。而目前的UCS-4规范中还没有任何字符被分配在BMP之外。



1. Unicode的编码方式是什么? 肯定不是依次往后排....


参考:
http://blog.csdn.net/xujinsmile/article/details/8526387
http://www.ruanyifeng.com/blog/2007/10/ascii_unicode_and_utf-8.html
http://www.freebuf.com/articles/others-articles/25623.html
http://www.cnblogs.com/kex1n/p/4138427.html
https://zh.wikipedia.org/wiki/Unicode#.E7.BC.96.E7.A0.81.E6.96.B9.E5.BC.8F
http://www.unicode.org/glossary/#supplementary_planes
https://zh.wikipedia.org/wiki/UTF-16
https://zh.wikipedia.org/wiki/UTF-8
https://zh.wikipedia.org/wiki/Unicode字符平面映射
http://www.ruanyifeng.com/blog/2014/12/unicode.html