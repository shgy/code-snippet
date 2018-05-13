代码部署到服务器上之后，内存溢出了。 这说明要么有大对象， 要么对象数量太多，垃圾收集器来不及释放。 一般来说是有大对象。

怎么办呢？

step1: 将出现异常时的Tomcat的堆转储文件弄出来。
```
 jmap -dump:format=b,file=cheap.bin <pid>
```

step2: 使用`eclipse memory analyzer`进行分析。 

mat的使用教程这里就略过。
