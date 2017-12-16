```
When killing an app, app first moves to KILLING state, If RMAppAttempt receives the 
attempt_unregister event before attempt_kill event, it'll ignore the later attempt_kill event. 
Hence, RMApp won't be able to move to KILLED state and stays at KILLING state forever.
```
RMApp进入Killing状态后， 如果接收到了 attempt_unregister事件， 那么就会忽略 attempt_kill事件， 永远停留在
Killing状态了。 有点盗梦空间的感觉。


YARN-2853 这个Bug比较复杂， 涉及到状态机的问题。 如果希望复现这个bug， 其过程如下：

1. 准备好两个版本的hadoop源码： 2.5.2 和 2.6.0

2. 使用2.6.0中的testCase去跑2.5.2的代码。

复制hadoop-2.6.0的源码，重命名为hadoop_25, 然后将分支切换到2.5.2
``` 
git reset --hard HEAD
git checkout release-2.5.2
mvn clean install -DskipTests
mvn compile -Pvisualize
dot -Tpng NodeManager.gv > NodeManager.png
dot -Tpng ResourceManager.gv > ResourceManager.png
dot -Tpng MapReduce.gv > MapReduce.png
```
然后基于2.5.2的源码跑2.6.0的testCase，
```
package  shgy;
import org.apache.hadoop.yarn.api.protocolrecords.FinishApplicationMasterRequest;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.AbstractEvent;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.event.Dispatcher;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.resourcemanager.MockAM;
import org.apache.hadoop.yarn.server.resourcemanager.MockNM;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppState;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEvent;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttemptState;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.mockito.ArgumentMatcher;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

/**
 * Created by shgy on 17-11-17.
 */
public class TestMockRMDemo {

    private static YarnConfiguration conf = null;

    public static YarnConfiguration getConf() {

        conf = new YarnConfiguration();
        conf.set(YarnConfiguration.RM_SCHEDULER,
                CapacityScheduler.class.getName());

        return conf;
    }

    public static void main(String[] args) throws Exception {
        final Dispatcher dispatcher = new AsyncDispatcher() {
            @Override
            public EventHandler getEventHandler() {

                class EventArgMatcher extends ArgumentMatcher<AbstractEvent> {
                    @Override
                    public boolean matches(Object argument) {
                        if (argument instanceof RMAppAttemptEvent) {
                            if (((RMAppAttemptEvent) argument).getType().equals(
                                    RMAppAttemptEventType.KILL)) {
                                return true;
                            }
                        }
                        return false;
                    }
                }

                EventHandler handler = spy(super.getEventHandler());
                doNothing().when(handler).handle(argThat(new EventArgMatcher()));
                return handler;
            }
        };

        MockRM rm1 = new MockRM(getConf()){
            @Override
            protected Dispatcher createDispatcher() {
                return dispatcher;
            }
        };
        rm1.start();
        MockNM nm1 =
                new MockNM("127.0.0.1:1234", 8192, rm1.getResourceTrackerService());
        nm1.registerNode();
        RMApp app1 = rm1.submitApp(200);
        MockAM am1 = MockRM.launchAndRegisterAM(app1, rm1, nm1);

        rm1.killApp(app1.getApplicationId());

        FinishApplicationMasterRequest req =
                FinishApplicationMasterRequest.newInstance(
                        FinalApplicationStatus.SUCCEEDED, "", "");
        am1.unregisterAppAttempt(req,false);

        rm1.waitForState(am1.getApplicationAttemptId(), RMAppAttemptState.FINISHING);
        nm1.nodeHeartbeat(am1.getApplicationAttemptId(), 1, ContainerState.COMPLETE);
        rm1.waitForState(am1.getApplicationAttemptId(), RMAppAttemptState.FINISHED);
        rm1.waitForState(app1.getApplicationId(), RMAppState.FINISHED);
    }
}

```
果然，出现了如下的日志:
```
Attempt State is : FAILED
App : application_1511189238055_0001 State is : KILLING Waiting for state : KILLED
App : application_1511189238055_0001 State is : KILLING Waiting for state : KILLED
App : application_1511189238055_0001 State is : KILLING Waiting for state : KILLED
App : application_1511189238055_0001 State is : KILLING Waiting for state : KILLED
App : application_1511189238055_0001 State is : KILLING Waiting for state : KILLED
App : application_1511189238055_0001 State is : KILLING Waiting for state : KILLED
App : application_1511189238055_0001 State is : KILLING Waiting for state : KILLED
App : application_1511189238055_0001 State is : KILLING Waiting for state : KILLED
App : application_1511189238055_0001 State is : KILLING Waiting for state : KILLED
App : application_1511189238055_0001 State is : KILLING Waiting for state : KILLED
```
Bug复现了， 接下来就是追寻其原因了。RM端，app被kill后，RMApp状态机卡在了KILLING状态。

接下来：
1. 梳理用户提交App后， RM端RMApp状态机的变化。借此熟悉RMApp状态机变化的整个流程。
如何梳理呢？ 使用TestRM。