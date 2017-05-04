最开始, 在windows下安装Java, 不明白为什么JAVA_HOME, CLASSPATH, PATH要这么设置. 使用了Linux系统后, 一切都得到解释了.
```
export JAVA_HOME=/opt/jdk1.7.0_80
export CLASSPATH=.:${JAVA_HOME}/lib/dt.jar:${JAVA_HOME}/lib/tools.jar
export PATH=${JAVA_HOME}/bin:$PATH
```
CLASSPATH中的"."代表当前目录. ".."代表父目录.

```
public class Hello {
	public static void main(String[] args) {

		System.out.println("HelloWorld");
	}
}
```
使用C语言时, 代码是这样的
```
#include<stdio.h>

int main(int argc, char* argv[]){
  printf("hello world");
  return 0;
}
```


1. 程序的入口依然是熟悉的main, 只是main必须依附在一个类中. 这就是Java面向对象的规则.

2. 系统被抽象成了`System`类. 我们使用`System.out`对象提供的`println`服务实现将字符串输出的功能.

3. 程序使用的`System`类在`java.lang`包中, 这个包是默认引入的.

4. Java中所有用户定义的类默认继承Object类


初步了解一下`System`类, 看它提供了哪些核心的服务?
1. 标准输入输出
 提供了`in`, `out`, `err` 这三个静态域, 即标准输入/标准输出/标准出错.  这是封装操作系统提供的功能.
<Unix 环境高级编程> 中指出`按惯例, 每当运行一个新程序时, 所有的shell都为其打开三个文件描述符: 标准输出, 标准输入,标准出错`

2. 环境变量和系统属性
获取环境变量`System.getenv()`和系统属性`System.getProperty()` :
```
    // 启动时 -Dsolr.home=/home/solr/. 可以使用 man java 查看

	public static void main(String[] args) throws FileNotFoundException {
		System.out.println(System.getenv("JAVA_HOME"));
		System.out.println(System.getProperty("solr.home"));
	}
```

3. 加载动态链接库
`System.load()` 和 `System.loadLibrary()`和`System.mapLibraryName()`. 这个与JNI相关, 用得不多.

4. 安全管理
`System.getSecurityManager()`和`System.setSecurityManager()`

