```
package mockito.demo;

import java.util.Iterator;

import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WhenThenReturnDemo {
	@Test
	public void test(){
		Iterator i = mock(Iterator.class);
		/*
		 * 两种写法功能一样,并不是说一次返回多项数值
		 * */
		when(i.next()).thenReturn("Hello").thenReturn("world");
//		when(i.next()).thenReturn("Hello", "world"); 
		String result = i.next() + " " + i.next();
		System.out.println(result);

	}
}

```
