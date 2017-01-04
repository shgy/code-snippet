```
package shgy.namenode;

import org.apache.hadoop.util.ProgramDriver;

public class ProgramDriverDemo {
	public static class Test{
		public static void main(String[] args) {
			System.out.println("test");
		}
	}

	public static class Test2{
		public static void main(String[] args) {
			System.out.println("test2 " + args.length);
		}
	}

	public static void main(String[] args) throws Throwable {
		ProgramDriver pgd = new ProgramDriver();
		pgd.addClass("class1", Test.class, "test");
		pgd.addClass("class2", Test2.class, "test");
		args = new String[]{"class2"}; // 只少要带一个参数, 以选择要执行的类
		pgd.run(args);
	}
}
```