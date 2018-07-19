写mr程序，免不了打包，将程序放到服务器上执行。 通常， 打包时我们不需要mr相关的包，因为服务器上有。 怎么办呢？
```
<dependency> 
   <groupId>xxx</groupId> 
   <artifactId>xx</artifactId> 
   <version>xxx</version> 
   <scope>provided</scope> 
</dependency> 
```
provided是目标容器已经provide这个artifact。换句话说，它只影响到编译，测试阶段。在编译测试阶段，我们需要这个artifact对应的jar包在classpath中，而在运行阶段，假定目标的容器（比如我们这里的liferay容器）已经提供了这个jar包，所以无需我们这个artifact对应的jar包

参考：
http://blog.51cto.com/supercharles888/981316
