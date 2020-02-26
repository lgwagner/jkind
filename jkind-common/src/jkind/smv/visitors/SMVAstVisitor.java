package jkind.smv.visitors;

import jkind.lustre.Node;
import jkind.lustre.Program;
import jkind.smv.SMVModule;
import jkind.smv.SMVBinaryExpr;
import jkind.smv.SMVBoolExpr;
import jkind.smv.SMVEquation;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVIntExpr;
import jkind.smv.SMVProgram;
import jkind.smv.SMVVarDecl;

public interface SMVAstVisitor<T, S extends T> extends SMVExprVisitor<S>{

	public T visit(Program program);

	public T visit(SMVProgram smvprogram);

	public T visit(SMVModule module);

	public T visit(Node node);

	public T visit(SMVVarDecl smvVarDecl);

	public T visit(SMVEquation smvEquation);

//	public T visit(SMVBinaryExpr sMVBinaryExpr);
//	
//	public T visit(SMVBoolExpr sMVBoolExpr);
//	
//	public T visit(SMVIntExpr sMVIntExpr);
//	
//	public T visit(SMVIdExpr sMVIdExpr);
	
	public S visit(SMVBinaryExpr sMVBinaryExpr);
	
	public S visit(SMVBoolExpr sMVBoolExpr);
	
	public S visit(SMVIntExpr sMVIntExpr);
	
	public S visit(SMVIdExpr sMVIdExpr);

}
