学习编写一个可运行在yarn之上的框架, 对于学习Hive的引擎: map-reduce/tez/spark 非常重要. 但是YARN应用程序的开发, 步骤比较繁琐,
涉及的知识点比较多, 并不像通常学习一个hello world这么简单. hadoop提供了两个案例: DistributedShell和UnManaged AM.
首先, 了解一下这两个Yarn应用的功能, 运行一下.

```
$ hadoop jar share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.6.0.jar \
org.apache.hadoop.yarn.applications.distributedshell.Client \
--jar share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.6.0.jar  \
--shell_command ls \
--num_containers 10 \
--container_memory 350 \
--master_memory 350 \
--priority 10
```

在哪里查看执行的结果呢?
```
$ tree /opt/hadoop-2.6.0/logs/userlogs/
/opt/hadoop-2.6.0/logs/userlogs/
├── application_1479282320505_0001
│   ├── container_1479282320505_0001_01_000001
│   │   ├── stderr
│   │   ├── stdout
│   │   └── syslog
│   ├── container_1479282320505_0001_01_000002
│   │   ├── stderr
│   │   ├── stdout
│   │   └── syslog
│   └── container_1479282320505_0001_01_000003
│       ├── stderr
│       ├── stdout
│       └── syslog
```
在stdout文件中可查看执行的结果.

如果希望查看这一功能背后的运行机制, 那就需要在代码中调用该程序了.
maven pom.xml如下:
```
<dependency>
  <groupId>org.apache.hadoop</groupId>
  <artifactId>hadoop-yarn-applications-distributedshell</artifactId>
   <version>2.6.0</version>
</dependency>
<dependency>
    <groupId>org.apache.hadoop</groupId>
    <artifactId>hadoop-minicluster</artifactId>
    <version>2.6.0</version>
</dependency>
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.2</version>
</dependency>
```
Java代码如下:
```
package yarn.dsdemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.JarFinder;
import org.apache.hadoop.yarn.applications.distributedshell.ApplicationMaster;
import org.apache.hadoop.yarn.applications.distributedshell.Client;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;

public class MiniYarnClusterDemo {
	  protected final static String APPMASTER_JAR =
		      JarFinder.getJar(ApplicationMaster.class);
	public static void main(String[] args) throws Exception {
		YarnConfiguration conf = new YarnConfiguration();
		conf.setInt(YarnConfiguration.RM_SCHEDULER_MINIMUM_ALLOCATION_MB, 128);
		conf.set("yarn.log.dir", "target");
		conf.setBoolean(YarnConfiguration.TIMELINE_SERVICE_ENABLED, true);
		conf.set(YarnConfiguration.RM_SCHEDULER, CapacityScheduler.class.getName());
		MiniYARNCluster yarnCluster = new MiniYARNCluster("testapp", 1, 1, 1, 1, true);
		yarnCluster.init(conf);

		yarnCluster.start();

		int sec = 60;
		while (sec >= 0) {
			if (yarnCluster.getResourceManager().getRMContext().getRMNodes().size() >= 1) {
				break;
			}
			Thread.sleep(1000);
			sec--;
		}

		 URL url = Thread.currentThread().getContextClassLoader().getResource("yarn-site.xml");
	      if (url == null) {
	        throw new RuntimeException("Could not find 'yarn-site.xml' dummy file in classpath");
	      }
	      Configuration yarnClusterConfig = yarnCluster.getConfig();
	      yarnClusterConfig.set("yarn.application.classpath", new File(url.getPath()).getParent());
	      //write the document to a buffer (not directly to the file, as that
	      //can cause the file being written to get read -which will then fail.
	      ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
	      yarnClusterConfig.writeXml(bytesOut);
	      bytesOut.close();
	      //write the bytes to the file in the classpath
	      OutputStream os = new FileOutputStream(new File(url.getPath()));
	      os.write(bytesOut.toByteArray());
	      os.close();
	    FileContext fsContext = FileContext.getLocalFSFileContext();
	    fsContext
	        .delete(
	            new Path(conf
	                .get("yarn.timeline-service.leveldb-timeline-store.path")),
	            true);
	    try {
	      Thread.sleep(2000);
	    } catch (InterruptedException e) {
	      // LOG.info("setup thread sleep interrupted. message=" + e.getMessage());
	    }

		String[] args2 = {
		        "--jar",
		        "/opt/hadoop-2.6.0/share/hadoop/yarn/hadoop-yarn-applications-distributedshell-2.6.0.jar",
		        "--num_containers",
		        "2",
		        "--shell_command",
		        "date",
		        "--master_memory",
		        "512",
		        "--master_vcores",
		        "2",
		        "--container_memory",
		        "128",
		        "--container_vcores",
		        "1",
		        "--debug"
		    };
		 Client client = new Client(new Configuration(yarnCluster.getConfig()));
		 client.init(args2);
		 client.run();

	}
}
```

上面的代码, 都是从hadoop的TestCase中抽取出来的.