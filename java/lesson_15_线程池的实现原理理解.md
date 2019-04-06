池技术是性能优化的重要手段：连接池，线程池已经是开发中的标配了。面试中这个知识点也是高频问题。抽空学习了Java的ThreadPoolExecutor, 把学习的思路记录一下。

由于线程的创建和销毁都是系统层面的操作，涉及到系统资源的占用和回收，所以创建线程是一个重量级的操作。为了提升性能，就引入了线程池；即线程复用。Java不仅提供了线程池，还提供了线程池的操作工具类。 我们由浅入深了解一下。

```
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadDemo {


    static class Worker implements Runnable{


        public void run(){
                System.out.println("run work "+Thread.currentThread().getName() );
        }
    }

    public static void main(String[] args) {

        Worker w1 = new Worker();

        ExecutorService service = Executors.newFixedThreadPool(10);

        service.submit(w1);

        service.shutdown();
    }
}

```
看Executors的源码，发现其使用的是ThreadPoolExecutor。 研究一下ThreadPoolExecutor, 发现其默认的参数`Executors.defaultThreadFactory(), defaultHandler`。线程池的创建工厂默认如下:
```
       public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
```
也就是自定义了一下线程的名字，将线程归到了同一个组。
线程池的`defaultHandler`如下:
```
    public static class AbortPolicy implements RejectedExecutionHandler {
        /**
         * Creates an {@code AbortPolicy}.
         */
        public AbortPolicy() { }

        /**
         * Always throws RejectedExecutionException.
         *
         * @param r the runnable task requested to be executed
         * @param e the executor attempting to execute this task
         * @throws RejectedExecutionException always.
         */
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new RejectedExecutionException("Task " + r.toString() +
                                                 " rejected from " +
                                                 e.toString());
        }
    }
```

也就是说，当提交的任务超过线程池的容量，那么就会抛出`RejectedExecutionException`异常。 但是使用Executors会发现，并没有抛出异常。这是因为Executors创建`BlockingQueue`时没有指定队列的容量。

换言之，线程池能容纳的任务数量最多为`maximumPoolSize` + `queueSize`。 比如线程池如下`new ThreadPoolExecutor(10, 11, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(5));` 则最大任务数量为16个，超过16个就会抛出异常。

线程池中线程数量有多少呢？先运行如下的代码:
```
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadDemo {


    static class Worker implements Runnable{


        public void run(){

            try {
                Thread.sleep(1000);
                System.out.println("done work "+Thread.currentThread().getName() );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {

        Worker w1 = new Worker();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 4,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(5));
        for(int i=0;i<9;i++) {
            executor.submit(w1);
        }

        executor.shutdown();
    }
}

```
可以发现最多开启了4个线程。 这4个线程就对应了4个Worker的实例。
看worker的源码可以发现，它兼备AQS和Runnable两个特性。 我们只关注它Runnable的特性。
```
while (task != null || (task = getTask()) != null) {
                w.lock();
                // If pool is stopping, ensure thread is interrupted;
                // if not, ensure thread is not interrupted.  This
                // requires a recheck in second case to deal with
                // shutdownNow race while clearing interrupt
                if ((runStateAtLeast(ctl.get(), STOP) ||
                     (Thread.interrupted() &&
                      runStateAtLeast(ctl.get(), STOP))) &&
                    !wt.isInterrupted())
                    wt.interrupt();
                try {
                    beforeExecute(wt, task);
                    Throwable thrown = null;
                    try {
                        task.run();
                    } catch (RuntimeException x) {
                        thrown = x; throw x;
                    } catch (Error x) {
                        thrown = x; throw x;
                    } catch (Throwable x) {
                        thrown = x; throw new Error(x);
                    } finally {
                        afterExecute(task, thrown);
                    }
                } finally {
                    task = null;
                    w.completedTasks++;
                    w.unlock();
                }
            }
```
在这个线程中不断从队列中获取任务，然后执行。 worker中反复出现的`ctl`又是什么呢？

ctl是两个变量组合，一个32位的int, 高3位用于控制线程池的状态，低29位用于记录线程池启动线程的数量。
所以有这么几个方法
```
    // Packing and unpacking ctl
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }
```

整个线程池的核心，就`worker`和`ctl`的理解。 有点复杂，主要是集中了:
```
1. AQS
2. BlockingQueue
```
这也是为什么我建议先学AQS,后学线程池的实现原理。



