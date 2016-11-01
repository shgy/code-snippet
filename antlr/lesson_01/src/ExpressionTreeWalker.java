// $ANTLR 2.7.2: "expressions.g" -> "ExpressionTreeWalker.java"$

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;
import java.lang.Math;

public class ExpressionTreeWalker extends antlr.TreeParser       implements ExpressionParserTokenTypes
 {
public ExpressionTreeWalker() {
	tokenNames = _tokenNames;
}

	public final double  expr(AST _t) throws RecognitionException {
		double r;
		
		AST expr_AST_in = (AST)_t;
		AST i = null;
		double a,b; r=0;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PLUS:
			{
				AST __t25 = _t;
				AST tmp1_AST_in = (AST)_t;
				match(_t,PLUS);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t25;
				_t = _t.getNextSibling();
				r=a+b;
				break;
			}
			case MINUS:
			{
				AST __t26 = _t;
				AST tmp2_AST_in = (AST)_t;
				match(_t,MINUS);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t26;
				_t = _t.getNextSibling();
				r=a-b;
				break;
			}
			case MUL:
			{
				AST __t27 = _t;
				AST tmp3_AST_in = (AST)_t;
				match(_t,MUL);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t27;
				_t = _t.getNextSibling();
				r=a*b;
				break;
			}
			case DIV:
			{
				AST __t28 = _t;
				AST tmp4_AST_in = (AST)_t;
				match(_t,DIV);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t28;
				_t = _t.getNextSibling();
				r=a/b;
				break;
			}
			case MOD:
			{
				AST __t29 = _t;
				AST tmp5_AST_in = (AST)_t;
				match(_t,MOD);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t29;
				_t = _t.getNextSibling();
				r=a%b;
				break;
			}
			case POW:
			{
				AST __t30 = _t;
				AST tmp6_AST_in = (AST)_t;
				match(_t,POW);
				_t = _t.getFirstChild();
				a=expr(_t);
				_t = _retTree;
				b=expr(_t);
				_t = _retTree;
				_t = __t30;
				_t = _t.getNextSibling();
				r=Math.pow(a,b);
				break;
			}
			case INT:
			{
				i = (AST)_t;
				match(_t,INT);
				_t = _t.getNextSibling();
				r=(double)Integer.parseInt(i.getText());
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return r;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"SEMI",
		"PLUS",
		"MINUS",
		"MUL",
		"DIV",
		"MOD",
		"POW",
		"INT",
		"DIGIT"
	};
	
	}
	
