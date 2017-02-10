在Hadoop的测试用例中看到`Whitebox.getInternalState()`方法, 写了一个简单的用例后,就明白其用法了.
```
package mockito.demo;

import org.mockito.internal.util.reflection.Whitebox;

class A{
	private String aa;
	public A(){
		this.aa = "hello world";
	}
}

public class WhiteboxDemo {
	public static void main(String[] args) {
		A a = new A();
		String aa = (String) Whitebox.getInternalState(a, "aa");
		System.out.println(aa);
	}
}

```
通过它, 能拿到对象的private类型的field, 感觉很神奇. 利用了反射机制. 看来, Java的封装只是一种约定.
