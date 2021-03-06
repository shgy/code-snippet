
Lucene最初设计是实现全文检索功能, 即只处理字符串. 因此, 在处理数值时, Lucene也是将数值编码为字符串。

将数值转换成字符串, Lucene-5.2.0对应的实现类为`org.apache.lucene.util.NumericUtils`。

其编码的方式如下：

1. Int类型的编码：
```
public static void main(String[] args){
	BytesRefBuilder act = new BytesRefBuilder();
	NumericUtils.intToPrefixCodedBytes(1, 0, act);
	
	BytesRef ref = act.get();
	System.out.println(ref.length);
}
```
可以发现NumericUtils把Int类型编码为6byte. 其中的1byte用于区别原数据类型为Int还是Long, 

```
SHIFT_START_INT  = 0x60;
SHIFT_START_LONG = 0x20;
```
另外的5byte表示原数. 我们知道, Int是32位, 即4byte. 为什么这里需要5byte呢? 

我们先思考另一个问题: 如何将Int转码成字符串, 并且保证其顺序呢?
即如果两个整数x,y编码成字符串a,b 要保证: Integer.compare(x,y) = String.compare(a,b)

首先,整数的取值范围(-2147483648,2147483647). 

插一句, 除去符号位, -2147483648的补码与0的补码是一样的, 实际上2147483648是溢出了的. 换个角度 -2147483648 = -0
关于0和-2147483648的编码，可以看出，除符号位外，两者是一样的
```
public static void intToBytesRef(){
		
		BytesRefBuilder act1 = new BytesRefBuilder();
		NumericUtils.intToPrefixCodedBytes(Integer.MIN_VALUE, 0, act1);
		BytesRef ref1 = act1.get();
		System.out.println(ref1);
		
		BytesRefBuilder act2 = new BytesRefBuilder();
		NumericUtils.intToPrefixCodedBytes(0, 0, act2);
		BytesRef ref2 = act2.get();
		System.out.println(ref2.toString());
}
```


OK, 思路回到`如何将Int转码成字符串, 并且保证其顺序`的问题. 如果我们单独只关注正数和负数, 那么会发现:

对于正数, 其补码范围为: `0x00 00 00 01`(1)到`0x7f ff ff ff`(2147483647), 是有序的, 保证了: Integer.compare(x,y) = String.compare(a,b).

对于负数, 其补码范围为: `0x80 00 00 00`(-2147483648)到`0xff ff ff ff`(-1), 是有序的, 保证了: Integer.compare(x,y) = String.compare(a,b).

使用python的struct包, 可以很方便地查看一个整数的补码:
```
>>> from struct import *
>>> pack('>i',-2147483648)
'\x80\x00\x00\x00'
>>> pack('>i',0)
'\x00\x00\x00\x00'
```
如果希望直接查看32-bit的二进制码, 如下:
```
>>>"".join([bin(ord(i))[2:].rjust(8,'0') for i in pack('>i', -2)])
'11111111111111111111111111111110'
```

还有一个问题: 从整体上, 负数得到的编码是大于正数得到的编码, 这就不符合`Integer.compare(x,y) = String.compare(a,b)`. 如何处理这一情况呢?
```
int sortableBits = val ^ 0x80000000;
```
采用二进制数的`异域`操作, 将正整数与负整数的符号位交换一下即可. 这样就可以保证整数编码后的字符串整体有序了. 所以这里取名`sortableBits`

接下来就回到 将Int编码为5-byte的问题. `For that integer values (32 bit or 64 bit) are made unsigned and the bits are converted to ASCII chars with each 7 bit.`即每7bit为了一个单位.

这是因为Lucene保存Unicode时使用的是UTF-8编码，这种编码的特点是，unicode值为0-127的字符使用一个字节编码。其实我们可以把32位的int看出5个7位的整数，这样的utf8编码就只有5个字节了.

到这里, 再看`NumericUtils.intToPrefixCodedBytes()`的代码就会很清晰了.
```
  public static void intToPrefixCodedBytes(final int val, final int shift, final BytesRefBuilder bytes) {
    // ensure shift is 0..31
    if ((shift & ~0x1f) != 0) {
      throw new IllegalArgumentException("Illegal shift value, must be 0..31; got shift=" + shift);
    }
    int nChars = (((31-shift)*37)>>8) + 1;    // i/7 is the same as (i*37)>>8 for i in 0..63
    bytes.setLength(nChars+1);   // one extra for the byte that contains the shift info
    bytes.grow(NumericUtils.BUF_SIZE_LONG);  // use the max
    bytes.setByteAt(0, (byte)(SHIFT_START_INT + shift));
    int sortableBits = val ^ 0x80000000;
    sortableBits >>>= shift;
    while (nChars > 0) {
      // Store 7 bits per byte for compatibility
      // with UTF-8 encoding of terms
      bytes.setByteAt(nChars--, (byte)(sortableBits & 0x7f));
      sortableBits >>>= 7;
    }
  }
```

关于`shift`参数, 由于是前缀编码`PrefixCodedBytes`, shift用于处理前缀问题,与本文讨论的主题无关, 暂不考虑.

最后还有一个细节问题
```
 int nChars = (((31-shift)*37)>>8) + 1;    // i/7 is the same as (i*37)>>8 for i in 0..63
```
为什么会采用如此费解的计算表达式呢? 首先把shift去掉
```
 int nChars = ((31*37)>>8) + 1;
```
将除法操作用乘法及位移操作来表示, 是CPU友好的一种表达方式.说人话就是:可以提升性能.
如果对汇编有了解，就可以理解得更清晰一些．乘法指令(mul)和位移指令(shl/shr)都是单操作数指令，而除法指令(div)是多操作数指令，相比而言需要消耗更多的CPU指令周期． [注: 在Ubuntu上简单测试后, 发现这里的理解有误, 这种写法, 对性能影响微弱, 可能是作者的习惯而已 ]

参考:
```
 http://blog.csdn.net/zhufenglonglove/article/details/51700898
```






