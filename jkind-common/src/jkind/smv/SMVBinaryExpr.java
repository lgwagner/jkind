package jkind.smv;

import jkind.Assert;
import jkind.smv.visitors.SMVExprVisitor;

public class SMVBinaryExpr extends SMVExpr{

	public final SMVExpr left;
	public final SMVBinaryOp op;
	public final SMVExpr right;
	
	public SMVBinaryExpr(SMVExpr left, SMVBinaryOp op, SMVExpr right) {
		Assert.isNotNull(left);
		Assert.isNotNull(op);
		Assert.isNotNull(right);
		this.left = left;
		this.op = op;
		this.right = right;
	}
	
	
	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
	

}
