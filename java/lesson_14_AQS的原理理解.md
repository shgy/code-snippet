AbstractQueuedSynchronizer是JUC的核心框架，其设计非常精妙。 使用了Java的模板方法模式。 首先试图还原一下其使用场景:
对于排他锁，在同一时刻，N个线程只有1个线程能获取到锁；其他没有获取到锁的线程被挂起放置在队列中，待获取锁的线程释放锁后，再唤醒队列中的线程。

线程的挂起是获取锁失败时调用Unsafe.park()方法；线程的唤醒是由其他线程释放锁时调用Unsafe.unpark()实现。
由于获取锁，执行锁内代码逻辑，释放锁整个流程可能只需要耗费几毫秒，所以很难对锁的争用有一个直观的感受。下面以3个线程来简单模拟一下排他锁的机制。
```
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

public class AQSDemo {

    private static final Unsafe unsafe = getUnsafe();
    private static final long stateOffset;
    private static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe)field.get(null);

        } catch (Exception e) {
        }
        return null;
    }

    static{
        try{
            stateOffset = unsafe.objectFieldOffset
                    (AQSDemo.class.getDeclaredField("state"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile int state;

    private List<Thread> threads = new ArrayList<>();


    public void lock(){

        if(!unsafe.compareAndSwapInt(state,stateOffset,0,1)){
           // 有问题，非线程安全；只作演示使用
            threads.add(Thread.currentThread());
            LockSupport.park();
            Thread.interrupted();
        }
    }

    public void unlock(){
        state = 0;
        if(!threads.isEmpty()){
            Thread first = threads.remove(0);
            LockSupport.unpark(first);
        }
    }

    static class MyThread extends Thread{

        private AQSDemo lock;

        public MyThread(AQSDemo lock){
            this.lock = lock;
        }
        public void run(){
            try{
                lock.lock();
                System.out.println("run ");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        AQSDemo lock = new AQSDemo();
        MyThread a1 = new MyThread(lock);
        MyThread a2 = new MyThread(lock);
        MyThread a3 = new MyThread(lock);
        a1.start();
        a2.start();
        a3.start();
    }

}
```
上面的代码，使用park和unpark简单模拟了排他锁的工作原理。使用ArrayList屏蔽了链表多线程环境下链表的构造细节， 该代码实际上在多线程环境中使用是有问题的，发现了么？

通过上面的代码，能理解到多线程环境下，链表为什么能比ArrayList好使。

理解AQS, 其核心在于理解`state`和`head`, `tail`三个变量。换句话说，理解AQS, 只需理解`状态`和`链表实现的队列` 这两样东西。其使用方式就是，如果更新状态不成功，就把线程挂起，丢到队列中；其他线程使用完毕后，从队列中唤醒一个线程执行。 如果排队的线程数量过多，那么该谁首先获得锁就有讲究，不能暗箱操作，所以有公平和非公平两种策略。

越来越能理解 “编程功底，细节是魔鬼”，理解了上面的使用方式，只相当于理解了需求。那么实现上有那些细节呢？ 我们通过问答的方式来阐明。

问题1： `state`变量为什么要用volatile关键词修饰？

volatile是synchronized的轻量版本，在特定的场景下具备锁的特点`变量更新的值不依赖于当前值`， 比如`setState()`方法。 当volatile的场景不满足时，使用Unsafe.compareAndSwap即可。


问题2： 链表是如何保证多线程环境下的链式结构？

首先我们看链表是一个双向链表，我们看链表呈现的几个状态：
```
1. 空链表
    (未初始化)
head  -- null
tail  -- null

    or
   (初始化后)
head  -- Empty Node
tail  -- Empty Node


2. 只有一个元素的链表

head  -- Empty Node <->  Thread Node  -- tail

```
也就是说，当链表的不为空时， 链表中填充者一个占位节点。

学习数据结构，把插入删除两个操作弄明白，基本就明白这个数据结构了。我们先看插入操作`enq()`:
```
    private Node enq(final Node node) {
        for (;;) {
            Node t = tail;
            if (t == null) { // Must initialize
                if (compareAndSetHead(new Node()))
                    tail = head;
            } else {
                node.prev = t;
                if (compareAndSetTail(t, node)) {
                    t.next = node;
                    return t;
                }
            }
        }
    }

```

首先一个无限循环。 假如这个链表没有初始化，那么这个链表会通过循环的结构插入2个节点。 由于多线程环境下， compareAndSet会存在失败，所以通过循环保证了失败重试。 为了保证同步，要么依赖锁，要么通过CPU的cas。 这里是实现同步器，只能依赖cas。 这种编程结构，看AtomicInteger，会特别熟悉。

接下来看链表的删除操作。当线程释放锁调用`release()`方法时，AQS会按线程进入队列的顺序唤醒地一个符合条件的线程，这就是FIFO的体现。代码如下:
```
  public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
```
这里`unparkSuccessor()`里面的`waitStatus`我们先忽略。这样的话，线程会从阻塞的后面继续执行，从`parkAndCheckInterrupt()`方法中出来。
```
    final boolean acquireQueued(final Node node, int arg) {
        boolean failed = true;
        try {
            boolean interrupted = false;
            for (;;) {
                final Node p = node.predecessor();
                if (p == head && tryAcquire(arg)) {
                    setHead(node);
                    p.next = null; // help GC
                    failed = false;
                    return interrupted;
                }
                if (shouldParkAfterFailedAcquire(p, node) &&
                    parkAndCheckInterrupt())
                    interrupted = true;
            }
        } finally {
            if (failed)
                cancelAcquire(node);
        }
    }
```
由于唤醒的顺序是FIFO, 所以通常`p==head`条件是满足的。如果获取到锁，就把当前节点作为链表的head节点：`setHead(node)`, 原head节点从链表中断开，让GC回收`p.next=null`。 也就是说，链表的删除是从头开始删除，以实现FIFO的目标。

到这里，AQS的链表操作就弄清楚了。接下来的疑问就在节点的`waitStatus`里面。

问题: waitStatus的作用是什么？

在AQS, 实现了一个ConditionObject,  就像Object.wait/nofity必须在synchronized中调用一样， JUC实现了一个Object.wait/notify的替代品。这是另一个话题，这里不细说了，后面再研究一下。


最后，总结一下，本文简单分析了一下AQS的实现机制。主要参考ReentrantLock和论文《The java.util.concurrent Synchronizer Framework》。




































