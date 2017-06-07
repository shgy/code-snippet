RPCCallBenchmark的基准测试中, 用到了ThreadMXBean.
ThreadMXBean提供了如下的服务:
1. Thread CPU time 线程的CPU执行时间
2. 线程资源争用的监控.
3. 线程同步信息和死锁检测.
死锁检测算法：当任一进程Pj申请一个已被其他进程占用的资源ri时，进行死锁检测。检测算法通过反复查找进程等待表和资源分配表，
来确定进程Pj对资源ri的请求是否导致形成环路，若是，便确定出现死锁。

稍微多看了一点: java.lang.management包中, 有如下的MXBean

```
BufferPoolMXBean
ClassLoadingMXBean
CompilationMXBean
GarbageCollectorMXBean
MemoryManagerMXBean
MemoryMXBean
MemoryPoolMXBean
OperatingSystemMXBean
PlatformLoggingMXBean
RuntimeMXBean
ThreadMXBean
```

BufferPoolMXBean的demo.
```
public class ThreadMXBeanDemo {
    public static void main(String[] args) {
//        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
//        threadBean.findDeadlockedThreads();
        ByteBuffer buffer = ByteBuffer.allocateDirect(1024*1024);
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(1024*1024*20);
        List<BufferPoolMXBean> pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        for (BufferPoolMXBean pool : pools) {
            System.out.println("name:"+pool.getName());
            System.out.println("memory used:"+pool.getMemoryUsed());
            System.out.println("count:"+pool.getCount());
            System.out.println("total capacity:"+pool.getTotalCapacity());
        }
    }
}
输出:
name:direct
memory used:22020096
count:2
total capacity:22020096
name:mapped
memory used:0
count:0
total capacity:0

```
MemoryPoolMXBean: 这要了解java的内存分区
```
        List<MemoryPoolMXBean> mpool = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean memoryPoolMXBean : mpool) {
            System.out.println(memoryPoolMXBean.getName());
            System.out.println(memoryPoolMXBean.getPeakUsage().getUsed());
        }
输出:
=======================================
Code Cache
410816
PS Eden Space
2862872
PS Survivor Space
0
PS Old Gen
0
PS Perm Gen
3267416
```

上述这些内容属于JMX的知识范畴. 暂时浅尝辄止. 将注意力集中到hadoop的RPC 框架中.