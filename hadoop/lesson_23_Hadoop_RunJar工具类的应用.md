Step 1: 编译一个超级简单的Hello.java
```
public class Hello{

    public static void main(String[] args){

        System.out.println("hello");
   }

}
```
Step 2: 编译然后打包
```
$javac Hello.java
$jar cvf hello.jar Hello.class
```

Step 3: 使用RunJar工具执行jar文件
```
import org.apache.hadoop.util.RunJar;

public class RunJarDemo {
	public static void main(String[] args) throws Throwable {
		args = new String[]{
				"/home/shgy/hello.jar",
				"Hello"
		};
		new RunJar().run(args);
	}
}

```