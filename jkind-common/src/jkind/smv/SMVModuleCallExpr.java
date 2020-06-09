package jkind.smv;

import java.util.Arrays;
import java.util.List;

import jkind.Assert;
import jkind.smv.visitors.SMVExprVisitor;
import jkind.util.Util;

public class SMVModuleCallExpr extends SMVExpr {
	public final String module;
	public final List<SMVExpr> args;

	public SMVModuleCallExpr(String module, List<SMVExpr> args) {
		Assert.isNotNull(module);
		this.module = module;
		this.args = Util.safeList(args);
	}
	
	public SMVModuleCallExpr(String module, SMVExpr... args) {
		this(module, Arrays.asList(args));
	}

	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
