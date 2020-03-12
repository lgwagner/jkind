package jkind.smv.visitors;

import jkind.smv.SMVEquation;
import jkind.smv.SMVModule;
import jkind.smv.SMVProgram;
import jkind.smv.SMVVarDecl;

public interface SMVAstVisitor<T, S extends T> extends SMVExprVisitor<S>{
	public T visit(SMVEquation smvEquation);
	public T visit(SMVModule smvModule);
	public T visit(SMVProgram smvProgram);
	public T visit(SMVVarDecl smvVarDecl);
}
