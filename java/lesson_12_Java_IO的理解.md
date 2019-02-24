回想最开始学习Java IO相关的操作时， 被各种Reader/Stream绕晕。 现在再回头梳理这一块的知识点，感觉清晰了很多。 Java作为编程语言，
大部分东西都是从系统层面带来的， 所以学习的知识点虽然在Java, 但是背后的答案却在操作系统层面。

首先理解核心概念:IO， 究竟什么是IO? 所谓IO就是内存与外设相关的数据传输。常用的外设有硬盘，网卡，打印机, 鼠标...
我们接触最频繁的IO操作是硬盘上文件的读写，所以学习IO基本上都是以文件操作为例子。IO作为操作系统的核心，知识点相当庞杂，如果没有合适的切入点，容易迷失其中。 

如果一定要找一个切入点，学习的先后顺序，个人建议如下:

1. RandomAccessFile

对于文件的操作，最适合是RandomAccessFile: 它能读能写能定位。 使用RandomAccessFile, 就是把文件当作一个数组，只不过这个数组是在硬盘上而已。读写文件就像操作数组一样。更难能可贵的是， RandomAccessFile封装了Java所有的基础类型, 可以说基本满足操作单个文件的使用需求了。

```
    public static void main(String[] args) throws IOException {


        RandomAccessFile bigArray = new RandomAccessFile(new File("/home/shgy/a.txt"),"rw");
        // 写
        bigArray.seek(10);
        bigArray.writeUTF("hello,world");
        
        System.out.println("filePointer at " + bigArray.getFilePointer());
        // 读
        bigArray.seek(10);
        System.out.println(bigArray.readUTF());
        bigArray.close();
    }
```
RandomAccessFile类有14个写的方法，17个读的方法，2个定位方法，2个长度相关操作，1个获取当前文件游标`getFilePointer()`的方法， 1个关闭文件释放系统资源的方法。 剩下的两个方法`getChannel()` 和 `getFD()`。`getChannel()` 跟NIO相关，`getFD()`是系统文件描述符，就本文所要总结的内容而言，已经超出三界之外，不在五行之中，暂时略过不提。

当文件数量多了以后，必然面临管理的问题。无论是windows还是Linux都采用层级管理，最后形成目录树。这带来一个新的问题就是文件的路径以及文件的归类。 面对这样的需求，Java提供了Files类来解决。通过了解Files类提供的API, 可以看出，其功能特点在于粗粒度的文件读写及文件属性的管理。 
使用Files来读写文件更简单:
```
    public static void main(String[] args) throws IOException {

        Files.write("hello world", new File("/home/shgy/a.txt"),Charset.defaultCharset());

        System.out.println(Files.readLines(new File("/home/shgy/a.txt"), Charset.defaultCharset()));
    }
```

Files 可以读写文件，可以重命名文件，可以读取设置文件属性，简直是瑞士军刀般的存在。这里涉及了更多文件相关的知识点，如果有学习过《鸟哥的Linux私房菜》第七章，再学习代码操作文件，就不会那么困惑了。 

使用Files操纵文件引出了一个新的知识点`Charset`, 即字符集。字符集产生的原因很简单: 人类语言是字符形式，计算机只能以字节的方式存储数据，字符跟字节之间得有个映射关系。比如上例中存储的`hello world`, 实际上存储的内容可以使用vim的`xdd`命令查看:
```
// vim  + %!xdd 命令即可

00000000: 6865 6c6c 6f20 776f 726c 640a            hello world.
```
关于字符集的知识，可以参考阮一峰的《字符编码笔记：ASCII，Unicode 和 UTF-8》。


理解了字符集，再进入Java的IO模块，才顺理成章。前面已经说过，所谓IO，就是内存与计算机外设的数据传输。Java从语言层面对IO进行了抽象, 这个抽象就是Steam, 数据流。这样的话，无论数据来源是文件，网页，内存块还是其他，都以一种统一的视角和处理方式看待。 所以Java定义了InputStream和OutputStream。
InputStream用于将数据读入内存, 对应的操作是read; OutputStream用于将数据写入外设，对应的操作是write。InputStream和OutputStream操纵的数据只能是字节或者字节数组， 这样就不用关心数据是文本，图片，音频，视频了，毕竟不管什么类型的数据，最终的呈现形式就是字节流。
这样，文件的操作就相当繁琐了：
```
public static void main(String[] args) throws IOException {

        // 读取文件
        FileInputStream fis = new FileInputStream(new File("/home/shgy/a.txt"));
        byte[] bytes = new byte[1024];
        int n = fis.read(bytes);
        if(n>0){
            System.out.println(new String(bytes,0,n));
        }
        fis.close();
        
        // 写文件
        FileOutputStream fos = new FileOutputStream(new File("/home/shgy/a.txt"));
        fos.write("hello, world".getBytes());
        fos.close();
}
```
鉴于我们处理的文件，绝大部分都是字符类型的文件，而且以字节的方式操纵字符确实过于原始，于是Java也定义了字符IO, 即Reader/Writer。
```
    public static void main(String[] args) throws IOException {

        // 读取文件
        FileReader fr = new FileReader(new File("/home/shgy/a.txt"));
        char[] buf = new char[1024];
        int n = fr.read(buf);
        System.out.println(new String(buf,0,n));
        fr.close();

        FileWriter fw = new FileWriter(new File("/home/shgy/a.txt"));
        fw.write("hello, world");
        fw.close();
    }
```

由于计算机本质是处理字节，所以字符和字节之间需要一个桥梁，这个就是InputStreamReader/OutputStreamWriter. 为了应对各种字符集和字节之间的编码解码，所以定义了StreamEncoder/StreamDecoder。

对于文件的读写，由于是需要操作硬盘或者网卡；考虑到安全性， 在系统层面需要系统调用，由用户态切入内核态。这个操作代价较高。所以又添加了一层缓冲，即BufferedInputStream/BufferedOutputStream 和 BufferedReader/BufferedWriter。

整个IO操作在InputStream/OutputStream和Reader/Writer基础之上丰富多彩起来。

由于外设，比如硬盘和网络数据的传输效率相比CPU的处理效率相差太远， 在《性能之颠》中有这样一个让人影响深刻的对比:
1个CPU周期为0.3ns, 1次机械磁盘IO周期为1～10ms, 1次从旧金山到纽约的互联网传输需要40ms; 由于时间单位太小，我们没有概念。 我们放大一下，假如:
1个CPU周期为1s, 则一次机械磁盘IO周期为1～12个月，1次从旧金山到纽约的互联网传输需要4年。 在这样一个差距面前，如何提高IO的效率，就显得尤为重要，
这就是NIO的由来。

在《UNIX网络编程卷1：套接字联网API》一书中总结了5种IO模型: 阻塞，非阻塞，IO复用，信号驱动，异步IO。Java的NIO是采用了IO复用(select)模型。

NIO处理数据，方式跟Stream有所不同。 Stream比较碎，以字节为最小粒度； NIO以数据块为最小粒度。所以可以避免数据的反复搬运，更高效，操作起来就更繁琐一些。
```
// 使用NIO写数据到文件
    public static void main(String[] args) throws IOException {

        FileOutputStream fos = new FileOutputStream(new File("/home/shgy/a.txt"));

        FileChannel fc = fos.getChannel();

        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.put("hello,world".getBytes());
        buf.flip();
        fc.write(buf);
        System.out.println("file channel position is " + fc.position());
        fos.close();

    }
```

NIO有如下的几个优点:
1. channel是支持读写的，所以相比Stream更灵活。
2. buffer可以分配堆外内存，这个对于IO来说，避免了数据从堆内存中倒腾一边，也避免了Java的GC, 性能自然有提升。
3. 对于网络IO, NIO可以在同一个线程同时监听多个端口，避免了创建多个线程和线程管理的开销。


由于IO这一块的知识点过于庞杂，不是一篇博客能说清楚的，这里只是简单梳理一下学习思路。





































如果使用Files类，基本上会面临一个困惑: Path和File怎么转换？
```
        File file = new File("/home/shgy/a.txt");
        Path path = file.toPath();

        Path path2 = Paths.get("/home/shgy/a.txt");
        File file2 = path2.toFile();
```

由于Java7启用了NIO, File对象基本上就是个过时的东西。但由于File又太基础了，所以不能抛弃，只能做兼容。


通过RandomAccessFile和Files两个类，我们可以应付绝大部分IO需求了。 通常情况下，IO的核心操作有如下的5个: open/write/read/seek/close.






