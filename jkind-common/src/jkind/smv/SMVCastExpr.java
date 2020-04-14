package jkind.smv;

import jkind.Assert;
import jkind.smv.visitors.SMVExprVisitor;

public class SMVCastExpr extends SMVExpr {

	public final SMVType type;
	public final SMVExpr expr;

	public SMVCastExpr(SMVType type, SMVExpr expr) {
		Assert.isNotNull(type);
		Assert.isNotNull(expr);
		this.type = type;
		this.expr = expr;
	}

	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
