package jkind.smv.visitors;

import jkind.smv.SMVArrayAccessExpr;
import jkind.smv.SMVArrayExpr;
import jkind.smv.SMVArrayUpdateExpr;
import jkind.smv.SMVBinaryExpr;
import jkind.smv.SMVBoolExpr;
import jkind.smv.SMVCaseExpr;
import jkind.smv.SMVCastExpr;
import jkind.smv.SMVFunctionCallExpr;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVInitIdExpr;
import jkind.smv.SMVIntExpr;
import jkind.smv.SMVNextIdExpr;
import jkind.smv.SMVRealExpr;
import jkind.smv.SMVUnaryExpr;

public interface SMVExprVisitor<T> {
	public T visit(SMVArrayAccessExpr e);
	public T visit(SMVArrayExpr e);
	public T visit(SMVArrayUpdateExpr e);
	public T visit(SMVBinaryExpr e);
	public T visit(SMVBoolExpr e);
	public T visit(SMVCastExpr e);
//	public T visit(CondactExpr e);
	public T visit(SMVFunctionCallExpr e);
	public T visit(SMVIdExpr e);
	public T visit(SMVInitIdExpr e);
	public T visit(SMVNextIdExpr e);
	public T visit(SMVCaseExpr e);
	public T visit(SMVIntExpr e);
//	public T visit(SMVModuleCallExpr e);
	public T visit(SMVRealExpr e);
//	public T visit(RecordAccessExpr e);
//	public T visit(RecordExpr e);
//	public T visit(RecordUpdateExpr e);
//	public T visit(TupleExpr e);
	public T visit(SMVUnaryExpr e);
}
