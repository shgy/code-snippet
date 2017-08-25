功能需求。 每个container向HDFS的test文件中写入hello world字符串。

这就需要改写distributedshell的Client和ApplicationMaster.

目前处理的一部分

```
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.client.api.YarnClientApplication;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.MiniYARNCluster;

import java.io.*;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by shgy on 17-7-3.
 */
public class StartMiniYarnCluster {

    public static void main(String[] args) throws IOException, YarnException {
        MiniYARNCluster yarnCluster = new MiniYARNCluster("testapp", 1, 1, 1, 1, true);
        YarnConfiguration conf = new YarnConfiguration();
        conf.set("yarn.log.dir", "target");
        yarnCluster.init(conf);
        yarnCluster.start();
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

        YarnClient yarnClient = YarnClient.createYarnClient();
        yarnClient.init(conf);
        yarnClient.start();

        YarnClientApplication app = yarnClient.createApplication();
        System.out.println(app.getNewApplicationResponse().getApplicationId());
        ApplicationSubmissionContext appContext = app.getApplicationSubmissionContext();
        appContext.setApplicationName("testapp");

        // 设置可用资源
        Resource capability = Resource.newInstance(10, 2);
        appContext.setResource(capability);

        // 设置ApplicationMaster启动container的相关参数，
        ContainerLaunchContext amContainer = ContainerLaunchContext.newInstance(
                null, null, Arrays.asList(new String[]{"echo hello"}), null, null, null);
        appContext.setAMContainerSpec(amContainer);
        yarnClient.submitApplication(appContext);

    }
}

```

   