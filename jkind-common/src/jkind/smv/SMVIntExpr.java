package jkind.smv;

import java.math.BigInteger;

import jkind.Assert;
import jkind.smv.visitors.SMVExprVisitor;

public class SMVIntExpr extends SMVExpr{
	
	public final BigInteger value;

	public SMVIntExpr(BigInteger value) {
		Assert.isNotNull(value);
		this.value = value;
	}
	
	public SMVIntExpr(int value) {
		this(BigInteger.valueOf(value));
	}

	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
