学习Yarn也有一段日子了。 感觉，如果我要写Yarn的入门教程， 就用Container作为切入点。私以为从Container入手，
可以最快速地学到如何编写类似于MapReduce这样基于Yarn的应用， 避免在前期被灌输了大量关于Yarn的概念，却不会写一行代码的窘况。

总结了几个原因

1. Container是任务的执行者， 在操作系统中表现为进程。 方便以进程这一熟悉视角来理解Container的其他面。

2. Container执行代码由用户控制， 可以方便地从Hello World开始， 自行使用RPC与Client/NM/RM进行通信。
   对相关组件的理解都能通过代码来验证。 这个相当重要： 理论+实践。 避免很快遗忘。
 

Container的环境变量。
可以通过如下的代码， 打印出Container的环境变量。
```

public class Hello {

    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        System.out.println("HomeDirectory: "+fs.getHomeDirectory().toString());

        for(Environment each: Environment.values()){
            System.out.println(each.name() +":"+ System.getenv(each.name()));
        }

    }
}
```

这些环境变量的设置在`ContainerLaunch.sanitizeEnv()`方法中 


