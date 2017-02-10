```
/*
 *
<dependency>
    <groupId>org.openjdk.jol</groupId>
    <artifactId>jol-core</artifactId>
    <version>0.6</version>
</dependency>
 * 
 **/
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.vm.VM;
import static java.lang.System.out;
public class JOLDemo1 {
	 public static void main(String[] args) throws Exception {
	        out.println(VM.current().details());
	        out.println(ClassLayout.parseInstance(new A()).toPrintable());
	    }
	 
	    public static class A {
	        boolean f;
	        B b;
	        int[] a = new int[10];
	        public A(){
	        	this.b = new B();
	        }
	    }
	    
	    public static class B{
	    	int[] a = new int[100];
	    	public void print(){
	    		System.out.println(a);
	    	}
	    }
}
```
输出
```
# Using compressed klass with 3-bit shift.
# WARNING | Compressed references base/shifts are guessed by the experiment!
# WARNING | Therefore, computed addresses are just guesses, and ARE NOT RELIABLE.
# WARNING | Make sure to attach Serviceability Agent to get the reliable addresses.
# Objects are 8 bytes aligned.
# Field sizes by type: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]
# Array element sizes: 4, 1, 1, 2, 2, 4, 4, 8, 8 [bytes]

JOLDemo1$A object internals:
 OFFSET  SIZE    TYPE DESCRIPTION                    VALUE
      0     4         (object header)                01 00 00 00 (00000001 00000000 00000000 00000000) (1)
      4     4         (object header)                00 00 00 00 (00000000 00000000 00000000 00000000) (0)
      8     4         (object header)                a0 c1 c0 e8 (10100000 11000001 11000000 11101000) (-390020704)
     12     1 boolean A.f                            false
     13     3         (alignment/padding gap)        N/A
     16     4       B A.b                            (object)
     20     4   int[] A.a                            [0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
Instance size: 24 bytes
Space losses: 3 bytes internal + 0 bytes external = 3 bytes total
```

