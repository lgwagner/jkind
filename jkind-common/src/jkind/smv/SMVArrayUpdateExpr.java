package jkind.smv;

import jkind.Assert;
import jkind.smv.visitors.SMVExprVisitor;

public class SMVArrayUpdateExpr extends SMVExpr {
	public final SMVExpr array;
	public final SMVExpr index;
	public final SMVExpr value;

	public SMVArrayUpdateExpr(SMVExpr array, SMVExpr index, SMVExpr value) {
		Assert.isNotNull(array);
		Assert.isNotNull(index);
		Assert.isNotNull(value);
		this.array = array;
		this.index = index;
		this.value = value;
	}

	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
