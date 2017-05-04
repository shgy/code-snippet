学习<Hadoop技术内幕：深入解析YARN架构设计与实现原理>: 利用Hadoop的RPC框架实现简单的远程调用.

由于Java内置的RPC框架RMI比较厚重, 因此dong cutting为Hadoop重新开发了RPC框架.

RPC可以说是Hadoop的大动脉, 各个组件都是通过RPC进行通信.


如何使用这一功能呢?

step 1: 在pom.xml文件中添加依赖项
```
  <dependency>
       <groupId>org.apache.hadoop</groupId>
       <artifactId>hadoop-common</artifactId>
       <version>2.6.0</version>
   </dependency>
```

step 2: 在src/main/resources中添加log4j.properties
```
log4j.rootLogger=DEBUG,console
log4j.additivity.org.apache=true
# (console)
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.Threshold=INFO
log4j.appender.console.ImmediateFlush=true
log4j.appender.console.Target=System.err
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=[%-5p] %d(%r) --> [%t] %l: %m %x %n
```

step 3: 定义RPC协议
```
package hadooprpc.demo;

import java.io.IOException;

interface ClientProtocol extends org.apache.hadoop.ipc.VersionedProtocol {
	// 版本号,默认情况下,不同版本号的 RPC Client 和 Server 之间不能相互通信
	public static final long versionID = 1L;
	String echo(String value) throws IOException;
	int add(int v1, int v2) throws IOException;
}
```

step 4: 实现RPC协议
```
package hadooprpc.demo;

import java.io.IOException;

import org.apache.hadoop.ipc.ProtocolSignature;

public  class ClientProtocolImpl implements ClientProtocol {
    // 重载的方法,用于获取自定义的协议版本号,
	public long getProtocolVersion(String protocol, long clientVersion) {
		return ClientProtocol.versionID;
	}

	// 重载的方法,用于获取协议签名
	public ProtocolSignature getProtocolSignature(String protocol, long clientVersion,
	int hashcode) {
		return new ProtocolSignature(ClientProtocol.versionID, null);
	}

	public String echo(String value) throws IOException {
		return value;
	}

	public int add(int v1, int v2) throws IOException {
		return v1 + v2;
	}
}
```

step 5； 构造并启动 RPC Server
```
package hadooprpc.demo;

import java.io.IOException;

import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;

public class RPCServer {
	public static void main(String[] args) throws HadoopIllegalArgumentException, IOException {
		Configuration conf = new Configuration();
		Server server = new RPC.Builder(conf).setProtocol(ClientProtocol.class)
				.setInstance(new ClientProtocolImpl()).setBindAddress("localhost").setPort(8097)
				.setNumHandlers(5).build();
				server.start();
	}
}

```

step 6: 构造 RPC Client 并发送 RPC 请求
```
package hadooprpc.demo;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

public class RPCClient {
	public static void main(String[] args) throws IOException {
		ClientProtocol client = (ClientProtocol)RPC.getProxy(
		ClientProtocol.class, ClientProtocol.versionID, new InetSocketAddress(8097), new Configuration());
		int result = client.add(5, 6);
		System.out.println(result);
		String echoResult = client.echo("result");
		System.out.println(echoResult);
		RPC.stopProxy(client); -- 关闭连接
	}
}
```

step 7: 启动RPC Server, 然后执行 RPC Client
