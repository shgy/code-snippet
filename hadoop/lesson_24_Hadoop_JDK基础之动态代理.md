<JAVA与模式> 中记录了代理模式的各种作用及表示形式.

需求1: 如何实现对List.add()方法的禁用?

与这个需求类似的是`Collections.unmodifiableList()`, 但是这个需求只禁用`List.add()`并不禁用`List.remove()`
可以使用`Proxy.newProxyInstances`方法, 案例如下:
```
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class Client {

	public static List getList(final List list) {
	    return (List) Proxy.newProxyInstance(Client.class.getClassLoader(),
									    	new Class[] { List.class },
									        new InvocationHandler() {
									            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
									                if ("add".equals(method.getName())) {
									                    throw new UnsupportedOperationException();
									                }
									                else {
									                    return method.invoke(list, args);
									                }
									            }
									        });
	 }

	public static void main(String[] args) {
		List list = getList(new ArrayList<String>());
		System.out.println(list.add(1));
	}
}
```

上面实现方式, 应用了JDK内置的动态代理机制. 实现了对ArrayList.add()方法的保护. 属于保护代理(Protect o Access).





动态代理是切面编程(AOP)的一种实现方式. 所谓AOP, 即在方法调用前后, 添加一些逻辑, 但是需要这些逻辑对用户是透明的.
例如: 统计程序中List.add()的调用次数, 在List.add()方法调用前后实现日志的记录....

先抛开`动态代理`这一奇技淫巧, 先了解为什么Java程序中需要`代理`这种技术

`每隔一段时间, 程序员社区就会开始考虑将"无所不在的对象"作为所有问题的解决之道`  -- <Java核心技术>

例如：　两台计算机的通信，　如果用＂无所不在的对象＂解决，　就是JAVA的RMI(Remote Method Invoke)技术了．
对于调用者而言, 由于真实的对象方法在服务器端, 所以只有在本机封装一个模块, 实现客户端与服务器的通信. 这个模块就是代理, 代理服务器的方法.














