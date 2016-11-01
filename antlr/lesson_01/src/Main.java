

import java.io.*;
import antlr.CommonAST;
import antlr.collections.AST;
import antlr.debug.misc.ASTFrame;
/*
 输入以 分号";"结尾, 如:
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