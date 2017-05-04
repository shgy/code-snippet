Yarn默认的Timeline Web接口是8088:
```
conf.set(YarnConfiguration.TIMELINE_SERVICE_WEBAPP_ADDRESS, "localhost:8089");
```
可修改默认的接口

单独启动一个`ApplicationHistoryServer`的代码如下:

```
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.applicationhistoryservice.ApplicationHistoryServer;
import org.apache.hadoop.yarn.server.timeline.MemoryTimelineStore;
import org.apache.hadoop.yarn.server.timeline.TimelineStore;

public class ApplicationHistoryServiceDemo {

    public static void main(String[] args) {
    	ApplicationHistoryServer historyServer = new ApplicationHistoryServer();
    	Configuration config = new YarnConfiguration();
    	config.setClass(YarnConfiguration.TIMELINE_SERVICE_STORE,
    	    MemoryTimelineStore.class, TimelineStore.class);
    	config.set(YarnConfiguration.TIMELINE_SERVICE_WEBAPP_ADDRESS, "localhost:0");
    	try {
    	  historyServer.init(config);

    	  historyServer.start();
    	  historyServer.stop();
    	} finally {
    	  historyServer.stop();
    	}
	}
}
```

Yarn的Timeline Server有两大职责:
一. 处于已完成状态的应用的通常信息
比如:
ApplicationSubmissionContext中存储的用户信息, applicationId 等应用级别(application level)的信息
每一次application-attempt的信息
每一个Container的信息
二. 每个正在运行或者已经完成的应用所属框架(Mapreduce/Tez,Spark)的信息
比如:
MapReduce框架的map任务数, reduce任务数.

TimelineClient用于和服务端进行交互.
```
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.api.records.timeline.TimelinePutResponse;
import org.apache.hadoop.yarn.client.api.TimelineClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.applicationhistoryservice.ApplicationHistoryServer;
import org.apache.hadoop.yarn.server.timeline.MemoryTimelineStore;
import org.apache.hadoop.yarn.server.timeline.TimelineStore;

public class ApplicationHistoryServiceDemo {

    public static void main(String[] args) {
    	ApplicationHistoryServer historyServer = new ApplicationHistoryServer();
    	Configuration config = new YarnConfiguration();
    	config.setBoolean(YarnConfiguration.TIMELINE_SERVICE_ENABLED, true);
//    	config.set(name, value);
    	config.setClass(YarnConfiguration.TIMELINE_SERVICE_STORE,
    	    MemoryTimelineStore.class, TimelineStore.class);
    	config.set(YarnConfiguration.TIMELINE_SERVICE_WEBAPP_ADDRESS, "localhost:8088");
    	try {
    	  historyServer.init(config);

    	  historyServer.start();
//    	  historyServer.stop();
    	} finally {
//    	  historyServer.stop();
    	}

    	TimelineClient client = TimelineClient.createTimelineClient();
		  client.init(config);
		  client.start();

		  TimelineEntity entity = new TimelineEntity();
		  // Compose the entity
		  try {
		    TimelinePutResponse response = client.putEntities(entity);
		    System.out.println(response);
		  } catch (IOException e) {
		    // Handle the exception
		  } catch (YarnException e) {
		    // Handle the exception
		  }
		  // Stop the Timeline client
		  client.stop();
	}
}
```