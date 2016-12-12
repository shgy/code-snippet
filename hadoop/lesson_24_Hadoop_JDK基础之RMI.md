
RMI编程很简单.

Step 1: 创建接口
```
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Hello extends Remote{

	String sayHello() throws RemoteException;

}
```

Step 2: 创建服务端实现类
```

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer implements Hello{

	@Override
	public String sayHello() throws RemoteException {
		// TODO Auto-generated method stub
		return "hello world";
	}

	public static void main(String[] args) throws RemoteException, AlreadyBoundException {
		System.setProperty("java.rmi.server.useCodebaseOnly","false");
		RMIServer s = new RMIServer();
		Hello stub = (Hello)UnicastRemoteObject.exportObject(s,0);
		Registry registry = LocateRegistry.getRegistry();

		registry.bind("hello", stub);

		System.out.println("Server ready");
	}
}
```

Step 3: 创建客户端实现类
```
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
	public static void main(String[] args) throws RemoteException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry("localhost");
		Hello stub = (Hello)registry.lookup("hello");
		String resp = stub.sayHello();
		System.out.println(resp);
	}
}
```

一般习惯在eclipse下开发. 接下来在eclipse中run一下RMIServer, 此时肯定会报错, 没关系, 我们的目的在于编译.
然后从shell中到target/classes目录下: [注意: 一定要在target/classes/目录下]
```
$ rmiregistry
```
然后再在eclipse中运行RMIServer, 一切正常, 然后再运行RMIClient即可.

初步感觉: Java的RMI不太好用.




参考: http://docs.oracle.com/javase/7/docs/technotes/guides/rmi/hello/hello-world.html