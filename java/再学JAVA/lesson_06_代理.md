通常调用一个类, 我们会这样做
 ```

interface ServiceA{
	public void say(String name);
}

class ServiceAImpl implements ServiceA{

	public void say(String name){
		System.out.println("hello world: " + name);
	}
}

public class ProxyTest{

	public static void main(String[] args) {

		ServiceA s = new ServiceAImpl();
		s.say("hello");

	}
}

```

如果希望在`say()`方法调用的前后做点啥? 比如打个日志,  记录一下 方法的执行时间 ...

 我们可以这样做

 ```
 class TraceHandler implements InvocationHandler{

	private Object target;

	public TraceHandler(Object t){
		this.target = t;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// TODO Auto-generated method stub
		System.out.println("before .... ");
		Object ret = method.invoke(target, args);
		System.out.println("after .... ");
		return ret;
	}

}


public class ProxyTest{

	public static void main(String[] args) {

		TraceHandler handler = new TraceHandler(new ServiceAImpl());
		Object proxy = Proxy.newProxyInstance(ServiceA.class.getClassLoader(), new Class[]{ServiceA.class},	 handler);

		((ServiceA)proxy).say(" proxy");
	}
}

 ```
这样做的好处在于, 调用每个接口申明的方法都会调用 TraceHandler.invoke() 方法.