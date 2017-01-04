在测试的时候, 我们通常只验证结果, 把测试对象当作黑盒. 但有时候需要知道代码运行的关键路径. 比如方法a调用的时候, 是否调用过方法b.
对于这样的需求, 可以通过mockito的spy和verify方法实现:
```
package mockito.demo;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Test;


public class SpyDemo {

	class InnerCall{
		
		public void process(boolean choosePath1){
			
			if(choosePath1){
				path1();
			}else{
				path2();
			}
		}
		
		public void path1(){
			
		}
		public void path2(){
			
		}
	}
	
	@Test
	public void showSpyDemo(){
		    
		InnerCall ic = new InnerCall();
		InnerCall spy = spy(ic);
		  
	    spy.process(true);
	    // 验证
	    verify(spy).path1();
	}
}
```

