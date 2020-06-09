package jkind.smv.visitors;

import java.util.List;

import jkind.smv.SMVAst;
import jkind.smv.SMVEquation;
import jkind.smv.SMVExpr;
import jkind.smv.SMVFunction;
import jkind.smv.SMVModule;
import jkind.smv.SMVProgram;
import jkind.smv.SMVVarDecl;

public class SMVAstMapVisitor extends SMVExprMapVisitor implements SMVAstVisitor<SMVAst, SMVExpr> {

	@Override
	public SMVEquation visit(SMVEquation e) {
		// Do not traverse e.lhs since they do not really act like Exprs
		return new SMVEquation(e.lhs, e.expr.accept(this));
	}

	@Override
	public SMVFunction visit(SMVFunction e) {
		List<SMVVarDecl> inputs = visitVarDecls(e.inputs);
		List<SMVVarDecl> outputs = visitVarDecls(e.outputs);
		return new SMVFunction(e.id, inputs, outputs);
	}

	@Override
	public SMVModule visit(SMVModule e) {
		List<SMVVarDecl> inputs = visitVarDecls(e.inputs);
		List<SMVVarDecl> outputs = visitVarDecls(e.outputs);
		List<SMVVarDecl> locals = visitVarDecls(e.locals);
		List<SMVEquation> equations = visitEquations(e.equations);
		List<SMVExpr> assertions = visitAssertions(e.assertions);
		List<String> properties = visitProperties(e.sMVSpecifications);
		return new SMVModule(e.id, inputs, outputs, locals, equations, assertions, properties);
	}

	protected List<SMVVarDecl> visitVarDecls(List<SMVVarDecl> es) {
		return map(this::visit, es);
	}

	protected List<SMVEquation> visitEquations(List<SMVEquation> es) {
		return map(this::visit, es);
	}

	protected List<SMVExpr> visitAssertions(List<SMVExpr> es) {
		return visitSMVExprs(es);
	}

	protected List<String> visitProperties(List<String> es) {
		return map(this::visitProperty, es);
	}

	protected String visitProperty(String e) {
		return e;
	}

	@Override
	public SMVProgram visit(SMVProgram e) {
		List<SMVFunction> functions = visitFunctions(e.functions);
		List<SMVModule> modules = visitModules(e.modules);
		return new SMVProgram(functions, modules, e.main);
	}

	protected List<SMVModule> visitModules(List<SMVModule> es) {
		return map(this::visit, es);
	}

	protected List<SMVFunction> visitFunctions(List<SMVFunction> es) {
		return map(this::visit, es);
	}

	@Override
	public SMVVarDecl visit(SMVVarDecl e) {
		return e;
	}

}
