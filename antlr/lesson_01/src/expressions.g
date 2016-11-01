class ExpressionParser extends Parser;
options { buildAST=true; }

expr     : sumExpr SEMI;
sumExpr  : prodExpr ((PLUS^|MINUS^) prodExpr)*; 
prodExpr : powExpr ((MUL^|DIV^|MOD^) powExpr)* ;
powExpr  : atom (POW^ atom)? ;
atom     : INT ;

class ExpressionLexer extends Lexer;

PLUS  : '+' ;
MINUS : '-' ;
MUL   : '*' ;
DIV   : '/' ;
MOD   : '%' ;
POW   : '^' ;
SEMI  : ';' ;
protected DIGIT : '0'..'9' ;
INT   : (DIGIT)+ ;

{import java.lang.Math;}
class ExpressionTreeWalker extends TreeParser;

expr returns [double r]
  { double a,b; r=0; }

  : #(PLUS a=expr b=expr)  { r=a+b; }
  | #(MINUS a=expr b=expr) { r=a-b; }
  | #(MUL  a=expr b=expr)  { r=a*b; }
  | #(DIV  a=expr b=expr)  { r=a/b; }
  | #(MOD  a=expr b=expr)  { r=a%b; }
  | #(POW  a=expr b=expr)  { r=Math.pow(a,b); }
  | i:INT { r=(double)Integer.parseInt(i.getText()); }
  ;