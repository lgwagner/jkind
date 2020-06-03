package jkind.smv;

import jkind.Assert;
import jkind.smv.visitors.SMVExprVisitor;

public class SMVArrayAccessExpr extends SMVExpr {
	public final SMVExpr array;
	public final SMVExpr index;
	
	public SMVArrayAccessExpr(SMVExpr array, SMVExpr index) {
		Assert.isNotNull(array);
		Assert.isNotNull(index);
		this.array = array;
		this.index = index;
	}
	
	public SMVArrayAccessExpr(SMVExpr array, int index) {
		this(array, new SMVIntExpr(index));
	}

	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}
}