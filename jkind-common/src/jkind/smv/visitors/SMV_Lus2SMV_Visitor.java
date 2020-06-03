package jkind.smv.visitors;

import jkind.lustre.Expr;
import jkind.lustre.Node;
import jkind.lustre.VarDecl;

public interface SMV_Lus2SMV_Visitor <T, S>{
	
	public T visit(Node node);
	public T visit(VarDecl varDecl);
	//public T visit(Equation equation);
	public T visit(Expr expr);
	//public T visit(Type type);

}
