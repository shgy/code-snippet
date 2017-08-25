AppMaster是Yarn启动的第一个Container, 它用于来启动其他的Container. 就像启动一个项目， 先要指定一个项目负责人，
然后项目由负责人全权负责。 AppMaster就有些类似于项目负责人的角色。

再来回顾一下 Yarn-App需要用到的3个RPC协议：

1. ApplicationClientProtocol:              Client  --> ResourceManager  submitApp/forceKillApp/monitorApp

2. ApplicationMasterProtocol:   ApplicationMaster  --> ResourceManager  registerApp/finishApp

3. ContainerManagementProtocol: ApplicationMaster  --> NodeManager      start/stop container

这3个协议， 我们从`ApplicationMasterProtocol`入手， 对unmanaged-am-launcher进行改造。

为什么要选择unmanaged-am-launcher呢？ 
它的TestCase将Client和ApplicationMaster角色写在一个类中。 貌似这是在告诉读者： Client/AppMaster
只是从逻辑上划分的角色， 实现上可以灵活处理。

改造很简单， 按角色将类拆分开来。
```
├── src
│   ├── main
│   │   └── java
│   │       └── shgy
│   │           └── yarn
│   │               ├── app
│   │               │   ├── ApplicationMaster.java
│   │               │   ├── Client.java
│   │               │   ├── DSConstants.java
│   │               │   ├── Hello.java
│   │               │   └── Log4jPropertyHelper.java
│   │               └── unmanaged
│   │                   ├── UnmanagedAMLauncher.java
│   │                   └── UnmanagedAppMaster.java
│   └── test
│       ├── java
│       │   ├── UnManagedDemo.java
│       │   └── YarnClientDemo.java
│       └── resources
│           ├── log4j.properties
│           └── yarn-site.xml
```
UnmanagedAppMaster.java 的功能很简单,  registerApp 然后 finishApp。 

UnmanagedAppMaster 用到了 ApplicationMasterProtocol 类。
UnManagedDemo      用到了 ApplicationClientProtocol 类。
由于没有启动worker Container， 所以， 没有用到ContainerManagementProtocol 类。

这里改造唯一值得一提的就是 UnmanagedAppMaster 需要连接到RM的rpc服务， 所以需要
将RPC服务依赖的host:port信息传递给UnmanagedAppMaster。这里为了简单， 采用命令行参数的方式。

```
  从miniyarncluster中取出集群启动后得到的端口信息
 InetSocketAddress appMasterAddr = conf.getSocketAddr(YarnConfiguration.RM_SCHEDULER_ADDRESS,
                YarnConfiguration.DEFAULT_RM_SCHEDULER_ADDRESS,
                YarnConfiguration.DEFAULT_RM_SCHEDULER_PORT);
        System.out.println(appMasterAddr.toString());

 testUMALauncher(appMasterAddr.getHostName(), appMasterAddr.getPort());
```
UnmanagedAppMaster中从main方法的args中得到端口信息。
```
package shgy.yarn.unmanaged;

import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.yarn.api.ApplicationMasterProtocol;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterResponse;
import org.apache.hadoop.yarn.api.protocolrecords.RegisterApplicationMasterRequest;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.client.ClientRMProxy;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 * Created by shgy on 17-7-13.
 */
public class UnmanagedAppMaster {
    /*
    *  这里需要把 RM的端口和地址通过参数传递过来
    * */
    public static void main(String[] args) throws Exception {

        System.out.println("start UnmanagedAppMaster");
        System.out.println(Arrays.asList(args));

        if (args[0].equals("success")) {
            String host= args[1];
            int port = Integer.parseInt(args[2]);
            YarnConfiguration conf = new YarnConfiguration();
            conf.setSocketAddr(YarnConfiguration.RM_SCHEDULER_ADDRESS, new InetSocketAddress(host,port));

            ApplicationMasterProtocol client = ClientRMProxy.createRMProxy(conf,
                    ApplicationMasterProtocol.class);
            client.registerApplicationMaster(RegisterApplicationMasterRequest
                    .newInstance(NetUtils.getHostname(), -1, ""));
            Thread.sleep(1000);
            FinishApplicationMasterResponse resp =
                    client.finishApplicationMaster(FinishApplicationMasterRequest
                            .newInstance(FinalApplicationStatus.SUCCEEDED, "success", null));
            if(!resp.getIsUnregistered()){
                throw new Exception(" resp.getIsUnregistered() return  "+ resp.getIsUnregistered() );
            }
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

}
```












