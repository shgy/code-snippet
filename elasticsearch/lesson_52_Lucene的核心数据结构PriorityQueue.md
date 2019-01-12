Luene的核心应用场景是全文检索。简单来说，就是通过用户输入的关键词来匹配相关文档，然后根据匹配程度返回TopN的查询结果给用户。 这里需要解决的一个核心问题就是如何快速返回TopN的结果，这本质上是一个排序的问题。说起排序，我们有很多选择，冒泡，快排，归并...。 这些排序算法在数据量小的时候，不是问题。一旦数据量过大，就成为问题了。

例如对1000万的数组排序:
```
        Integer[] a = new Integer[10000000];

        for(int i=0;i<10000000;i++){
            a[i] = (int) (Math.random()*10000000);
        }
        long start = System.currentTimeMillis();
        Arrays.sort(a);
        System.out.println((System.currentTimeMillis() - start) +" 毫秒");
```
在我的电脑耗时需要5秒左右， 这个等待时间对于用户体验来说，就不那么feeling good了。 


这时候，该考虑优化了。优化基本上是一个做减法的过程。再回到我们的核心需求: 基于搜索关键词返回TopN的结果。 也就是说，我们只需要TopN的结果有序就可以了。 基于上述需求，我们引入一个新的数据结构: 堆(Heap)。

堆是一种特殊的二叉树。所谓二叉树就是每个节点最多有两个子节点: 最多生二胎，超生不被允许的。

对于二叉树这种树形结构，最核心的关系就是父子节点关系。 定义不同的节点关系，我们就能得到丰富多彩的数据结构，以应对不同场景的业务问题。比如:

规定“子节点不能大于父节点”， 我们可以得出根节点是最大的节点， 得到大顶堆。

规定“子节点不能小于父节点”， 我们可以得出根节点是最小的节点， 得到小顶堆。

规定“根节点大于左子树，小于右子树；子树亦是如此”， 我们得到二叉搜索树；为了使二叉搜索树的左右尽量平衡，我们又得到了“红黑树”，“AVL树”，Treap等不同策略的平衡树。

这些概念性的东西，能理解就OK.

理解了堆的来龙去脉， 我们可能会有点困惑，它并没有直接维护一个有序的结构。 是的，它没有直接维护有序的结构，它是通过删除数据实现排序功能的。理解这一点特别重要。 以大顶堆为例: 由于堆顶是最大的元素，所以我们能确信，对于一个堆: 我们只要不断地删除堆顶的数据，直至空堆，就能得到一个有序的结果。这就是堆排序的思想。

那么如何利用堆实现TopN的有序输出呢？ 以搜索的打分作为排序项，我们希望输出得分最高的N个结果。 我们先遍历N个结果，得到有N个元素的小顶堆。由于堆顶的元素最小， 遍历剩下的打分结果，只需要跟堆的根节点对比即可。如果打分结果小于堆的根节点，弃之；如果打分结果大于堆的根节点，删除根节点；然后使用该打分结果更新到堆中。 这样最后这个堆就维护了我们想要的TopN。

例如对1000万的数据，我们给出最大的前100个数，代码如下:
```
Integer[] a = new Integer[10000000];

        for(int i=0;i<10000000;i++){
            a[i] = (int) (Math.random()*10000000);
        }
        long start = System.currentTimeMillis();

        PriorityQueue<Integer> pq = new PriorityQueue<Integer>(100) {
            @Override
            protected boolean lessThan(Integer t1, Integer t2) {
                return t1 < t2;
            }
        };

        for(int i=0;i<10000000;i++){
            pq.insertWithOverflow(a[i]);
        }
        Integer[] b = new Integer[100];
        for(int i=99;i>=0;i--){
            b[i] = pq.pop();
        }
        System.out.println((System.currentTimeMillis() - start) +" 毫秒");
        System.out.println(Arrays.asList(b));
```
这个耗时只需要50多毫秒。 这个性能差距几乎是100倍。可见堆这种数据结构在TopN这个场景下是多么适合。

其实JDK有自己基于堆实现的优先队列PriorityQueue, 为啥Lucene要再造一遍轮子呢？

JDK默认的PriorityQueue是可以自动扩展的，Lucene需要定长的。
JDK默认的PriorityQueue将数据结构封装得比较紧密，而Lucene需要一定的灵活性，比如调整堆顶。

小顶堆是一种二叉树，所以其逻辑结构大致如下:
```
    1
 3    2
5 8  7 6
```
如果观察，可以发现这个一个规律，就是第一层只有1个元素;第二层最多有2个元素; 第三层最多有4个元素， 即第N层有2^(n-1)个元素。 这个规律后面有用。

那么怎么编码实现一个堆呢？ 最简单的实现方式是基于数组，以Lucene的实现为例，学习一下:
```
public abstract class PriorityQueue<T> {
    private int size;
    private final int maxSize;
    private final T[] heap;
```

定义了一个数组。 只需要做如下的规定，那么就能满足對的逻辑结构:

```
1. heap[0]位空置不用。
2. heap[1]为根节点。
3. heap[2~3]为第二层，heap[4~7] 为第三层 ... heap[2^n ~ 2^(n+1)-1]为第n-1层
```
这样，元素在数组的哪个位置，我们就能知道它属于哪一层了。

接下来要解决的问题是: 
1. 如何插入一个元素到堆中？

假设前面有N个元素了， 那么代码很简单
```
    public final T add(T element) {
        ++this.size;
        this.heap[this.size] = element;
        this.upHeap(this.size);
        return this.heap[1];
    }
```
两步走： s1 将元素添加到尾巴上。 s2: 由于这个元素有可能比其父节点小，所以递归地跟其父节点比较，换位置即可，这里有点冒泡的感觉。即想象把乒乓球按入水中，松手后就会上浮。


2. 如何从堆中删除一个元素？

```
   public final T pop() {
        if (this.size > 0) {
            T result = this.heap[1];
            this.heap[1] = this.heap[this.size];
            this.heap[this.size] = null;
            --this.size;
            this.downHeap(1);
            return result;
        } else {
            return null;
        }
    }
```

两步走: s1: 用数组尾巴上的元素覆盖跟节点元素。 s2: 由于这个元素是否能胜任根节点这个位置还不确定，因此需要跟两个子节点比较，调整位置。这里有丝下沉的感觉。即想象把铁球丢入水中，自己就沉了下去。


这里，堆的插入和删除操作还是思路还是比较轻奇的，值得好好揣摩一番。

在Lucene中，PriorityQueue有那些应用场景呢？

1. HitQueue， 搜索打分的核心。
2. FieldValueHitQueue， 按字段排序的核心。
.... 

总之，该数据结构在Lucene中有30～40个子类，应用十分广泛。了解其实现机制，对于了解其他的功能大有裨益。











 





 

