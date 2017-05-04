使用Hive写SQL, 大部分功能集中在函数的使用,比如count,sum,min,max等.Hive内置的函数已经能够满足大部分业务场景.
如果希望针对特定的业务需求,实现自己的处理逻辑, 那么可以通过Hive中的UDF实现.
本文只是记录实现的框架, 即一个简单的UDF, 如果输入的字符串是"true", 则返回true, 否则返回false
1. 创建Maven项目, 自定义类extends UDF, 并实现一个名为evaluate()的方法 
```
package hive.learn;

import org.apache.hadoop.hive.ql.exec.UDF;

public class MyUdf extends UDF {
 
	public boolean evaluate(String text){
		return text!=null && text.equals("true");
	}
}

/* pom.xml
<dependency>
<groupId>org.apache.hive</groupId>
<artifactId>hive-exec</artifactId>
<version>1.1.1</version>
</dependency>



*/

```
2. 将该类打包,命名为my_udf.jar 使用eclipse的export功能即可完成打包.

3. 进入到Hive的命令行, 将打好的包添加到Hive中,使用如下的命令
```
add jar /home/shgy/hive_workspace/my_udf.jar;
```

4. 创建临时函数
```
create temporary function myfun as 'hive.learn.MyUdf';
```

5. 验证并使用函数
```
desc function myfun;
select myfun('true');
select myfun('asdf');
```

在hue中, 可能出现如下的错误.
```
Previous writer likely failed to write
hdfs://tx/tmp/hive/guangying/_tez_session_dir/93edf7e6-978c-4c5d-9315-d09d1faa6a99/hcbudf.jar.
Failing because I am unlikely to write too.
```
这应该是HUE的问题, 清空一下session即可.
