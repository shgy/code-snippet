java7在JSR 292中增加了对动态类型语言的支持，使java也可以像C语言那样将方法作为参数传递，
其实现在lava.lang.invoke包中。MethodHandle作用类似于反射中的Method类，但它比Method类要更加灵活和轻量级。

这类似于高阶函数.

python 是典型的动态语言. 例如, 假设有这样一个需求: 两个数的(平方根/绝对值...) 求和 . python可以这样实现
```
def add(x, y, f):
    return f(x) + f(y)
如果传入abs作为参数f的值：

add(-5, 9, abs)
根据函数的定义，函数执行的代码实际上是：

abs(-5) + abs(9)
由于参数 x, y 和 f 都可以任意传入，如果 f 传入其他函数，就可以得到不同的返回值。

任务
利用add(x,y,f)函数，计算：

import math

def add(x, y, f):
    return f(x) + f(y)

print add(25, 9, math.sqrt)
```

这在Java中,该如何实现呢?

```
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class HighOrderFunction<T extends Number> {

	public  Number add(T x, T y, MethodHandle handler) throws Throwable {
		return ((Number)handler.invoke(x)).doubleValue() + ((Number)handler.invoke(y)).doubleValue() ;
	}

	public static void main(String[] args) throws Throwable {

//		 MethodHandle handler =  MethodHandles.lookup().findStatic(Math.class, "double",
//				 MethodType.methodType(double.class, double.class)) ;

		 MethodHandle handler =  MethodHandles.lookup().findStatic(Math.class, "abs",
				 MethodType.methodType(int.class, int.class)) ;

		 System.out.println(new HighOrderFunction<Integer>().add(16, -4, handler));

	}
}
```
再对比python的实现
```
import math

def add(x, y, f):
    return f(x) + f(y)

print add(25, 9, math.sqrt)
```

这又是突出python 语法简洁的一个极好的例子.

参考:
http://blog.csdn.net/aesop_wubo/article/details/48858931
http://www.cnblogs.com/superxuezhazha/p/5714949.html