hadoop-yarn中, ResourceManager 和 NodeManager 中多处使用了状态机。 可以说： RPC和 状态机 是Hadoop的两块基石。

如何使用状态机呢？

假设有这样一个需求， 模拟一个人必须经历三种状态： Born  -- Run -- Dead 的一生。

 1. 初始化为Born状态。
 2. 出生后发送一个Prepare事件， 进入Run状态。
 3. 循环60次后发送一个Kill 事件， 进入Dead状态。

模仿了RMAppAttempImpl类， 实现了如下的代码。

```
import org.apache.hadoop.yarn.event.AbstractEvent;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.state.SingleArcTransition;
import org.apache.hadoop.yarn.state.StateMachine;
import org.apache.hadoop.yarn.state.StateMachineFactory;

/**
 * Created by shgy on 17-6-18.
 */

enum MyEventType
{
    START,
    KILL
}
class MyEvent extends AbstractEvent<MyEventType> {

    private int id;

    public MyEvent(int id,
                   MyEventType type) {
        super(type);
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}

enum MyState {
    NEW, RUNNING, DEAD
}


public class StateMachineDemo {
    private static class BaseTransition implements
            SingleArcTransition<StateMachineDemo, MyEvent> {

        @Override
        public void transition(StateMachineDemo demo,
                               MyEvent event) {
        }

    }
    static class MyLifeStartedTransition extends BaseTransition{

        @Override
        public void transition(StateMachineDemo demo, MyEvent event) {
            System.out.println("混世魔王出来了");
        }
    }

    static class MyLifeKilledTransition extends BaseTransition{
        @Override
        public void transition(StateMachineDemo demo, MyEvent event) {
            System.out.println("混世魔王死了");
        }
    }

    private static final StateMachineFactory<StateMachineDemo,
            MyState,
            MyEventType,
            MyEvent>
            stateMachineFactory  = new StateMachineFactory<StateMachineDemo,
                        MyState,
                        MyEventType,
                        MyEvent>(MyState.NEW)
            .addTransition(MyState.NEW, MyState.RUNNING, MyEventType.START, new MyLifeStartedTransition())
            .addTransition(MyState.RUNNING, MyState.DEAD, MyEventType.KILL, new MyLifeKilledTransition())
            .installTopology();


    private StateMachine<MyState,MyEventType,MyEvent> stateMachine;


    public StateMachineDemo(){
        this.stateMachine = stateMachineFactory.make(this);

    }

    public void live(){
        int age = 0;
        this.stateMachine.doTransition(MyEventType.START,new MyEvent(1,MyEventType.START));
        while (age++ < 60){
            try {

                System.out.println("the age is "+age);

                Thread.sleep(1000);
            }catch (InterruptedException e){}
        }

        this.stateMachine.doTransition(MyEventType.KILL, new MyEvent(1,MyEventType.KILL));
    }

    public static void main(String[] args) throws InterruptedException {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                new StateMachineDemo().live();
            }
        });

        t.start();
        t.join();

    }
}

```

使用dot命令可视化状态机
```
dot -Tpng mystatemachine.gv > mystatemachine.png
```