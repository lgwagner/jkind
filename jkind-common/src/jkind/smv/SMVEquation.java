package jkind.smv;

import java.util.Collections;
import java.util.List;

import jkind.Assert;
import jkind.lustre.IdExpr;
import jkind.smv.visitors.SMVAstVisitor;

public class SMVEquation extends SMVAst{
	
	public final List<SMVIdExpr> lhs;
	public final SMVExpr expr;

	public SMVEquation(List<SMVIdExpr> lhs, SMVExpr expr) {
		Assert.isNotNull(expr);
		this.lhs = lhs;
		this.expr = expr;
	}

	public SMVEquation(SMVIdExpr id, SMVExpr expr) {
		this.lhs = Collections.singletonList(id);
		this.expr = expr;
	}

	@Override
	public <T, S extends T> T accept(SMVAstVisitor<T, S> visitor) {
		return visitor.visit(this);
	}

}
