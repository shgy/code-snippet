启动MiniYarnCluster之后, 在浏览器中访问，RM的webapp会自动显示成NM的webapp. 设置只启动RM， 则webapp显示正常。
那么这肯定是webapp的问题， 有可能是单例造成的。 本文的目的就是追寻这个问题的原因。
在启动的过程中, 有这样一个warning信息。
```
Jul 17, 2017 12:16:30 PM com.google.inject.servlet.GuiceFilter setPipeline
WARNING: Multiple Servlet injectors detected. This is a warning indicating that you have more than one GuiceFilter running in your web application. 
If this is deliberate, you may safely ignore this message. If this is NOT deliberate however, your application may not work as expected.

```

1. 学习使用guice servlet开发web应用。
https://github.com/google/guice/wiki/Servlets

