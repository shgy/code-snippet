找了好久, 关于antlr的学习资料不多. 发现<The Definitive ANTLR 4 Reference, 2nd Edition > 比较适合入门, 就参照它.
本文记录它开篇的第一个例子.
首先, 制作通用的开发工具antlr和grun
```
$ cat /usr/local/bin/antlr4 
#!/bin/bash
java -cp "/opt/antlr/antlr-4.0-complete.jar" org.antlr.v4.Tool $*

$ cat /usr/local/bin/grun 
#!/bin/bash
java -cp ".:/opt/antlr/antlr-4.0-complete.jar" org.antlr.v4.runtime.misc.TestRig $*
```

需要注意grun的`-cp ".:/opt/antlr/antlr-4.0-complete.jar"`


然后编写Hello.g4
```
$ cat Hello.g4 
grammar Hello;
r: 'hello' ID;
ID: [a-z]+ ;
WS: [ \t\n\r]+ -> skip ;
```
然后, 使用do_all.sh中的命令编译, 运行
```
$ cat do_all.sh 
antlr4 Hello.g4
javac -cp /opt/antlr/antlr-4.0-complete.jar *.java
grun Hello r -tokens
```
输入`hello parrt`然后ctrl+d, 即可看到结果.
