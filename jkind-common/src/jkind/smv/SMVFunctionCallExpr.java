package jkind.smv;

import java.util.Arrays;
import java.util.List;

import jkind.Assert;
import jkind.smv.visitors.SMVExprVisitor;
import jkind.util.Util;

public class SMVFunctionCallExpr extends SMVExpr {

	public final String function;
	public final List<SMVExpr> args;

	public SMVFunctionCallExpr(String function, List<SMVExpr> args) {
		Assert.isNotNull(function);
		this.function = function;
		this.args = Util.safeList(args);
	}
	
	public SMVFunctionCallExpr(String node, SMVExpr... args) {
		this(node, Arrays.asList(args));
	}

	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
