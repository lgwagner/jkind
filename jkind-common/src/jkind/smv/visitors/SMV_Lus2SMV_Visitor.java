package jkind.smv.visitors;

import jkind.lustre.Node;
import jkind.lustre.Type;
import jkind.lustre.VarDecl;

public interface SMV_Lus2SMV_Visitor <T, S>{
	
	public T visit(Node node);
	public T visit(VarDecl node);
	//public T visit(Type type);

}
