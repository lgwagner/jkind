package jkind.smv.visitors;

import jkind.lustre.BinaryExpr;
import jkind.lustre.BoolExpr;
import jkind.smv.SMVBinaryExpr;
import jkind.smv.SMVBoolExpr;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVInitIdExpr;
import jkind.smv.SMVIntExpr;
import jkind.smv.SMVNextIdExpr;
import jkind.smv.SMVUnaryExpr;

public interface SMVExprVisitor<T> {
//	public T visit(ArrayAccessExpr e);
//	public T visit(ArrayExpr e);
//	public T visit(ArrayUpdateExpr e);
	public T visit(SMVBinaryExpr e);
	public T visit(SMVBoolExpr e);
	//public T visit(SMVBoolExpr e);
//	public T visit(CastExpr e);
//	public T visit(CondactExpr e);
//	public T visit(FunctionCallExpr e);
	public T visit(SMVIdExpr e);
	public T visit(SMVInitIdExpr e);
	public T visit(SMVNextIdExpr e);
//	public T visit(IfThenElseExpr e);
	public T visit(SMVIntExpr e);
//	public T visit(SMVNodeCallExpr e);
//	public T visit(SMVRealExpr e);
//	public T visit(RecordAccessExpr e);
//	public T visit(RecordExpr e);
//	public T visit(RecordUpdateExpr e);
//	public T visit(TupleExpr e);
	public T visit(SMVUnaryExpr e);
}
