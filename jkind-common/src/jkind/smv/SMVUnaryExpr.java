package jkind.smv;

import jkind.Assert;
import jkind.smv.visitors.SMVExprVisitor;

public class SMVUnaryExpr extends SMVExpr{

	public final SMVUnaryOp op;
	public final SMVExpr expr;

	public SMVUnaryExpr(SMVUnaryOp op, SMVExpr expr) {
		Assert.isNotNull(op);
		Assert.isNotNull(expr);
		this.op = op;
		this.expr = expr;
	}
	
	
	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
