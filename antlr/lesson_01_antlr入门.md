在学习Hive实现原理的时候, 了解到Hive是通过antlr来解析Hive的HQL. 想要理解Hive, antlr想必是绕不过去了.
Hive的精华即是通过将HQL解析成Mapreduce任务, 来进行数据操作.

antlr属于编译原理的范畴, 而编译原理一向让人敬而远之.为了简化问题, 首先从antlr入手, 以几个简单的例子来理解antlr的用法.

需求: 使用antlr解析四则运算表达式. 表达式只含有数字和运算符, 运算符只有6种: 加减乘除求余求幂(+-*/%^),没有括号，只有整数，没有小数。

可见问题已经十分简化了.

编译hive源码时,已经下载了antlr的jar包. 下载expression.g文件
```
wget http://supportweb.cs.bham.ac.uk/documentation/tutorials/docsystem/build/tutorials/antlr/files/expression.g
```
使用antlr生成相关的Java类
```
$ java -cp antlr-2.7.2.jar antlr.Tool expression.g
```
编译Main.java类
```
import java.io.*;
import antlr.CommonAST;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;
/*
 输入以分号";"结尾, 如:
 1+2*3;
 * */
public class Main {
  public static void main(String args[]) throws Exception {
    try {
      DataInputStream input = new DataInputStream(System.in);

      ExpressionLexer lexer = new ExpressionLexer(input); 

      ExpressionParser parser = new ExpressionParser(lexer);
      parser.expr();

      CommonAST parseTree = (CommonAST)parser.getAST();
      System.out.println(parseTree.toStringList());
      ASTFrame frame = new ASTFrame("The tree", parseTree);
      frame.setVisible(true);

      ExpressionTreeWalker walker = new ExpressionTreeWalker();
      double r = walker.expr(parseTree);
      System.out.println("Value: "+r);
    } catch(Exception e) { throw e;}
  }
}
```
运行,然后在控制台输入表达式,以";"结尾,即可看到结果.

关于antlr, 可以参考: http://supportweb.cs.bham.ac.uk/documentation/tutorials/docsystem/build/tutorials/antlr/antlr.html 看到更多的例子.
也可以从其它的大学搜索antlr的tutorials, 寻找更多入门的例子来练手.

---- 了解了一些antlr语法后, 关于expressions.g文件的理解
在expressions.g文件中, 每一个运算符后面都会带一个"^"符号, 如下:
```
expr     : sumExpr SEMI;
sumExpr  : prodExpr ((PLUS^|MINUS^) prodExpr)*; 
prodExpr : powExpr ((MUL^|DIV^|MOD^) powExpr)* ;
powExpr  : atom (POW^ atom)? ;
atom     : INT ;
```
其含义表示: PLUS和STAR记号是操作符，因此把它们作为子树的根结点，在它们后面注释上字符'^'。SEMI记号后缀有字符'!'，表明它不应该被加入到树中.
参考文档: http://blackproof.iteye.com/blog/1807372


