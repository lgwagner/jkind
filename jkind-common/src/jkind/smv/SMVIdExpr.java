package jkind.smv;

import jkind.Assert;
import jkind.smv.util.SMVValidId;
import jkind.smv.visitors.SMVExprVisitor;

public class SMVIdExpr extends SMVExpr {

	public final String id;

	public SMVIdExpr(String id) {
		Assert.isNotNull(id);
		String str = SMVValidId.replaceIllegalChar(id);
		this.id = id.replace(id, str);
	}

	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
