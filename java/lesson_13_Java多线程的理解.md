Java语言从设计之初就把多线程作为语言的核心, 至少从以下几点可以看出:

```
1. Object对象的wait和notify机制。
2. Thread类在lang包中。
3. synchronized volatile关键字。
```

虽然多线程是Java语言本身的特性，但是线程并不是Java语言独有的东西，而是操作系统的特性。Java在语言层面进行了封装，使其使用更简单。

多线程存在的价值在哪里呢？ 内存读写，磁盘IO， 网络传输的速率远远低于CPU处理数据的速度。所以在大部分场景下，CPU是闲置的。有了多线程，就可以更多地压榨CPU。所以，多线程在Web服务器，Lucene这类IO密集型的应用中大行其道。


多线程，知识点庞杂，但常用的核心知识点只有两个: 资源同步 和 线程池。

学习多线程，私以为这样的先后顺序比较好。

step1: 学习多线程的创建和运行， Thread类和Runnable接口, 通常自定义类是实现Runnable接口，并非Thread类。

```
public class ThreadDemo {

    static class Worker impelements Runnable{

        private String name;

        public Worker(String name){
            this.name = name;
        }

        public void run(){
            System.out.println(this.name+": "+Thread.currentThread().getName());
        }
    }

    public static void main(String[] args) {
        Thread a1 = new Thread(new Worker("nameA"));
        Thread a2 = new Thread(new Worker("nameB"));
        a1.start();
        a2.start();
    }
}

```


step2: 学习synchronized/volatile关键字


由于内存读写远比CPU执行的速度慢，出于提升性能的考虑，计算机设计者在CPU和内存之间架设了缓存，就是通常我们看到的L1, L2, L3。1个CPU周期需要0.3ns，L1缓存只需要0.9ns, 内存访问需要120ns。 差距达百倍, 可见缓存对性能的提升。如果我们在一个线程里读写一个变量，将初始值加到缓存后， CPU只需和缓存之间交互就可以了。

由于线程之间并不共享缓存，所以多个线程读写同一个变量时，就有可能出现不一致的情况。多个线程出现了信息的不对称， 如何解决这个问题呢？ 就像银行办理业务一样，每个人办理业务前先取个号。这个号就类似Java里面的锁机制。 synchronized就是Java的内置锁。 volatile可以看作synchronized的简化版， 因为它只能适用于某些特定的场景:
```
1. 修改变量不依赖其当前的值
2. 变量不跟其他变量一起对外作为整体
```
关于volatile的学习，个人觉得IBM的`Java theory and practice Managing volatility`是极好的学习资料。

step3: 学习ReentrantLock。了解了锁的使用场景后，可以先学习JUC实现的锁，最简单的就是ReentrantLock。ReentrantLock是互斥锁，可作为synchronized的代替品。
```
import java.util.concurrent.locks.ReentrantLock;

public class ThreadDemo {

    static class Counter implements Runnable{

        private int count;

        private ReentrantLock lock = new ReentrantLock();

        public void run(){
            try{
                lock.lock();
                count +=1;
            }finally {
                lock.unlock();
            }
        }

        public int getCount() {
            return count;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Counter a1 = new Counter();
        for(int i=0;i<1000;i++){
            new Thread(a1).start();
        }
        Thread.sleep(1000);
        System.out.println(a1.getCount());
    }
}

```
使用了ReentrantLock后，最好是能理解锁的实现原理，即大名顶顶的AQS。但是学习AQS之前，需要一些预备知识。
```
1. volatile关键字的使用场景及限制
2. Unsafe.compareAndSwap的使用
3. Unsafe.park/unpark的使用
4. 数据结构链表的原理
``` 

整个JUC的锁，线程安全的队列都是基于AQS构建的。 

step4: 学习AQS的原理。AQS基于链表构建的队列来存储争用锁的线程。当线程没有获取到锁，就会使用Unsafe.park将线程挂起；其他的线程释放锁时，就会调用Unsafe.unpark将挂起的线程恢复。

step5: 了解了AQS的原理后，再学习ReentrantReadWriteLock， Semaphore, Phase, CountDownLatch, CyclicBarrier 这些同步工具，就没那么难理解了。


step6: 学习BlockingQueue及其子类。 各种阻塞队列，使用频度比较高的是ArrayBlockingQueue, LinkedBlockingQueue.

step7: 学习线程池，主要是ThreadPoolExecutor类, ThreadPoolExecutor的核心是Worker类，每个Worker对应着一个线程。需要注意的是使用ThreadPoolExecutor时，阻塞队列一定要要指定大小。不然线程池的RejectPolicy就不起作用了。 具体的细节需要更深入的分析。

关于多线程的知识点， 一篇笔记是远远不够的，本文仅仅梳理学习的脉络。


参考文档:

https://www.ibm.com/developerworks/library/j-jtp06197/index.html












