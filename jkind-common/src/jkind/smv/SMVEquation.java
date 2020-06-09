package jkind.smv;

import jkind.Assert;
import jkind.smv.visitors.SMVAstVisitor;

public class SMVEquation extends SMVAst {

	public final SMVIdExpr lhs;
	public final SMVExpr expr;

	public SMVEquation(SMVIdExpr lhs, SMVExpr expr) {
		Assert.isNotNull(expr);
		this.lhs = lhs;
		this.expr = expr;
	}

	@Override
	public <T, S extends T> T accept(SMVAstVisitor<T, S> visitor) {
		return visitor.visit(this);
	}

}
