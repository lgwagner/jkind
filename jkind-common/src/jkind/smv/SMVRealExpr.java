package jkind.smv;

import java.math.BigDecimal;

import jkind.Assert;
import jkind.smv.visitors.SMVExprVisitor;

public class SMVRealExpr extends SMVExpr{

	public final BigDecimal value;

	public SMVRealExpr(BigDecimal value) {
		Assert.isNotNull(value);
		this.value = value;
	}
	
	
	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}	
}
