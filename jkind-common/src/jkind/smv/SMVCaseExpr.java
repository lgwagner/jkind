package jkind.smv;

import jkind.Assert;
import jkind.smv.visitors.SMVExprVisitor;

public class SMVCaseExpr extends SMVExpr {

	public final SMVExpr cond;
	public final SMVExpr thenExpr;
	public final SMVExpr elseExpr;

	public SMVCaseExpr(SMVExpr cond, SMVExpr thenExpr, SMVExpr elseExpr) {
		Assert.isNotNull(cond);
		Assert.isNotNull(thenExpr);
		Assert.isNotNull(elseExpr);
		this.cond = cond;
		this.thenExpr = thenExpr;
		this.elseExpr = elseExpr;
	}

	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
