package jkind.smv;

import jkind.Assert;
import jkind.smv.visitors.SMVExprVisitor;

public class SMVBoolExpr extends SMVExpr{

	public final boolean value;

	public SMVBoolExpr(boolean value) {
		Assert.isNotNull(value);
		this.value = value;
	}
	
	
	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
