1. 什么是线程?
在Linux系统中, 线程是与其他进程`共享`资源的进程.

如果对Linux操作系统实现有了解, 会有这样一个常识: 进程是fork出来的,如果更深入一点, 这个常识就进化成:进程是clone出来的.
正如人有五脏六腑, 进程也有许多资源(组成部分): 内核栈, thread_info, task_struct,文件系统信息, 打开的文件, 进程的地址空间,挂起的信号, 进程的状态.

这些资源的clone都是需要消耗CPU时间和内存空间的. 如果能够共享部分资源, 比如打开的文件和堆内存空间, 就可以极大提升系统性能.
共享打开的文件, 使得文件系统不必分配并维护新的文件描述符；共享堆内存空间使得进程间的通信(IPC)粒度更细更灵活.

因此, 线程又有另一个名字: 轻量级进程.  总之, 进程的出现是为了更进一步压榨CPU, 提升系统的性能.

2. Linux线程的实现

Linux当前采用的是:一对一模型. 由Red Hat提供的NLTP库. 即每个用户线程都对应各自的内核调度实体。内核会对每个线程进行调度，可以调度到其他处理器上面。
当然由内核来调度的结果就是：线程的每次操作会在用户态和内核态切换。另外，内核为每个线程都映射调度实体，如果系统出现大量线程，会对系统性能有影响。
但该模型的实用性还是高于多对一的线程模型。


(参考<UNIX环境高级编程> <Linux内核设计与实现> <Java并发编程实战>)

3. Java多线程的锁`AbstractQueuedSynchronizer`

```
Provides a framework for implementing blocking locks and related synchronizers (semaphores, events, etc) that rely on
first-in-first-out (FIFO) wait queues. This class is designed to be a useful basis for most kinds of synchronizers that
rely on a single atomic int value to represent state. Subclasses must define the protected methods that change this state,
and which define what that state means in terms of this object being acquired or released. Given these, the other methods
in this class carry out all queuing and blocking mechanics. Subclasses can maintain other state fields, but only the
atomically updated int value manipulated using methods getState(), setState(int) and compareAndSetState(int, int) is
tracked with respect to synchronization.
Subclasses should be defined as non-public internal helper classes that are used to implement the synchronization
properties of their enclosing class. Class AbstractQueuedSynchronizer does not implement any synchronization interface.
Instead it defines methods such as acquireInterruptibly(int) that can be invoked as appropriate by concrete locks and
related synchronizers to implement their public methods.

This class supports either or both a default exclusive mode and a shared mode. When acquired in exclusive mode,
attempted acquires by other threads cannot succeed. Shared mode acquires by multiple threads may (but need not) succeed.
This class does not "understand" these differences except in the mechanical sense that when a shared mode acquire
succeeds, the next waiting thread (if one exists) must also determine whether it can acquire as well. Threads waiting
in the different modes share the same FIFO queue. Usually, implementation subclasses support only one of these modes,
but both can come into play for example in a ReadWriteLock. Subclasses that support only exclusive or only shared modes
need not define the methods supporting the unused mode.

This class defines a nested AbstractQueuedSynchronizer.ConditionObject class that can be used as a Condition
implementation by subclasses supporting exclusive mode for which method isHeldExclusively() reports whether
synchronization is exclusively held with respect to the current thread, method release(int) invoked with the
current getState() value fully releases this object, and acquire(int), given this saved state value, eventually
restores this object to its previous acquired state. No AbstractQueuedSynchronizer method otherwise creates such
a condition, so if this constraint cannot be met, do not use it. The behavior of AbstractQueuedSynchronizer.
ConditionObject depends of course on the semantics of its synchronizer implementation.

This class provides inspection, instrumentation, and monitoring methods for the internal queue, as well as similar
methods for condition objects. These can be exported as desired into classes using an AbstractQueuedSynchronizer
for their synchronization mechanics.

Serialization of this class stores only the underlying atomic integer maintaining state, so deserialized objects
have empty thread queues. Typical subclasses requiring serializability will define a readObject method that restores
this to a known initial state upon deserialization.
```

一段一段理解:
1. 依赖FIFO队列为基础,实现了一个框架. 框架主打两个功能点: 阻塞的锁 和 同步机制(信号/事件)
2. 使用一个原子的int值作为状态(state)变量
3. 子类必须自行定义更改状态(state)的 `protected method`

4. AQS支持`exclusive mode`和`shared mode` (独占模式和共享模式),
5. 不同模式下等待的线程共享通一个FIFO队列.



一. Semaphore 计数信号量  典型的应用就是连接池.
二. CountDownLatch