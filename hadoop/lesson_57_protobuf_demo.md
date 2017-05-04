在安装Hadoop集群的时候已经安装过protobuf, 见`lesson_01_编译CLI_MiniCluster.md`

如何使用protobuf呢?

第一步: 编写 PersonMsg.proto 文件
```
message Person {

	// ID（必需）
	required int32 id = 1;

	// 姓名（必需）
	required string name = 2;

	// email（可选）
	optional string email = 3;

	// 朋友（集合）
	repeated string friends = 4;
}
```

第二步: 编译PersonMsg.proto文件, 生成Java Class
```
protoc --java_out=../java ./PersonMsg.proto
```

第三步: 编写简单的测试类
pom.xml
```
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>2.5.0</version>
</dependency>
```

TestProtobuf.java
```
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by shgy on 17-4-10.
 */


public class TestProtoBuf {
    public static void main(String[] args) throws IOException{
        // 按照定义的数据结构，创建一个Person
        PersonMsg.Person.Builder personBuilder = PersonMsg.Person.newBuilder();
        personBuilder.setId(1);
        personBuilder.setName("叉叉哥");
        personBuilder.setEmail("xxg@163.com");
        personBuilder.addFriends("Friend A");
        personBuilder.addFriends("Friend B");
        PersonMsg.Person xxg = personBuilder.build();

        // 将数据写到输出流，如网络输出流，这里就用ByteArrayOutputStream来代替
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        xxg.writeTo(output);

        // -------------- 分割线：上面是发送方，将数据序列化后发送 ---------------

        byte[] byteArray = output.toByteArray();

        // -------------- 分割线：下面是接收方，将数据接收后反序列化 ---------------

        // 接收到流并读取，如网络输入流，这里用ByteArrayInputStream来代替
        ByteArrayInputStream input = new ByteArrayInputStream(byteArray);

        // 反序列化
        PersonMsg.Person xxg2 = PersonMsg.Person.parseFrom(input);
        System.out.println("ID:" + xxg2.getId());
        System.out.println("name:" + xxg2.getName());
        System.out.println("email:" + xxg2.getEmail());
        System.out.println("friend:");
        List<String> friends = xxg2.getFriendsList();
        for(String friend : friends) {
            System.out.println(friend);
        }
    }
}
```

利用protobuf 可以方便的生成对象. 这在进行RPC编程时很有优势.



参考: http://blog.csdn.net/xiao__gui/article/details/36643949