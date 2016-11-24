在stackoverflow上看到的代码, 觉得相当经典
将float转换成二进制数
```
def float2bin(num):
  return ''.join(bin(ord(c)).replace('0b', '').rjust(8, '0') for c in struct.pack('!f', num))
```

已经有一个使用ieee-754表示的二进制字符串,如何转换成浮点数呢?

```
def bin2float(bits):
  return struct.unpack('f',struct.pack('I',int(bits,2)))

```

```
def float2bin(num):
  return ''.join(bin(ord(c)).replace('0b', '').rjust(8, '0') for c in struct.pack('!f', num))


def bin2float(bits):
  return struct.unpack('f',struct.pack('I',int(bits,2)))


```
