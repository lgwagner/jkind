package jkind.smv;

import java.util.List;

import jkind.smv.visitors.SMVExprVisitor;
import jkind.util.Util;

public class SMVArrayExpr extends SMVExpr {
	public final List<SMVExpr> elements;

	public SMVArrayExpr(List<SMVExpr> elements) {
		this.elements = Util.safeList(elements);
	}

	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
