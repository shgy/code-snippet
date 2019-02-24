看Java API的Object类， 一共11个方法。按使用的频度排名:

1. `toString()` 这个方法最常用在打日志，定位代码问题。


2. `equals()`和`hashCode()`, 这两个方法的使用经典例子是HashMap的源码

```
    public V put(K key, V value) {
        if (table == EMPTY_TABLE) {
            inflateTable(threshold);
        }
        if (key == null)
            return putForNullKey(value);
        int hash = hash(key);
        int i = indexFor(hash, table.length);
        for (Entry<K,V> e = table[i]; e != null; e = e.next) {
            Object k;
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
                V oldValue = e.value;
                e.value = value;
                e.recordAccess(this);
                return oldValue;
            }
        }

        modCount++;
        addEntry(hash, key, value, i);
        return null;
    }
```
`equals()` 比 `hashCode()` 要常用一点。


3. `wait()` 和 `notify()` 这个开发很少会直接用到，但是间接用到的场景不少，属于偏内功的点。`wait/notify`属于Object类最难理解的点了，因为它的基石是多线程。学习思路还是三步走。

step 1: 看文档说明

```
wait()
Causes the current thread to wait until another thread invokes the notify() method or the notifyAll() method for this object.

notify()
Wakes up a single thread that is waiting on this object's monitor.
```

这个文档说明，看完基本一头雾水，两个方法都提到了如下的核心概念`thread`, `object's monitor`。 先把这些概念放一边，看看是怎么用的。

step 2: 运行Demo

给个demo辅助理解，一个线程是干活的，另一个线程是管事的。管事的等活干完验收。
```
public class WaitNotifyDemo {

    static class Worker extends Thread{

        private volatile int status=0; // 1： 完成， -1： 出错

        @Override
        public void run(){


            try {
                System.out.println(Thread.currentThread().getName() +": 开始干活");
                Thread.sleep(3000);
                System.out.println(Thread.currentThread().getName() +": 完成任务");

                status = 1;
                // 通知主线程
                synchronized (this){
                    this.notify();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                status = -1;
            }
        }

        public int getStatus(){
            return status;
        }
    }


    public static void main(String[] args) throws InterruptedException {
        Worker worker= new Worker();
        worker.start();

        synchronized (worker){
            int status;
            while ((status=worker.getStatus())!=1){
                worker.wait();
                if(status==-1)
                    throw new RuntimeException("出错了");
            }
        }
        System.out.println("任务已经完成");
    }
}

```

step3: 折腾demo 

接下来， 我试了一下， 把notify的代码去掉，也能正常运行。 这就让人困惑了，文档明明说`必须调用notify，wait才能结束`。接下来再看文档：
```
A thread can also wake up without being notified, interrupted, or timing out, a so-called spurious wakeup
```
所以， wait方法必须在while循环中调用。好，解答了一点疑惑。但是每次Worker线程结束时没有调用notify，主线程就能正常退出, 这个也说不通。 唯一的解释是: JVM内部在某个地方调用了notify。看openjdk的源码， 果然如此:从start0开始， 定位到线程在退出时会调用`lock.notify_all(thread);`。只是这里的代码是JVM内部的代码，比较底层。 

其实，这个例子更简洁的写法是`worker.join()`。 如果看Thread.join()的源码，发现它的实现恰好就是调用了wait。 

总结一下，学习Java的API, 如果想理解透彻一点，估计绕不开JVM的c++源码。









