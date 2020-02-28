package jkind.smv;

import jkind.Assert;
import jkind.smv.visitors.SMVExprVisitor;

public class SMVNextIdExpr extends SMVExpr{
	
	public final String id;
	public final String init = "next";
	
	public SMVNextIdExpr(SMVIdExpr idExpr) {
		Assert.isNotNull(idExpr.id);
		this.id = init+"("+idExpr.id+")";
	}
	
	public SMVNextIdExpr(String id) {
		Assert.isNotNull(id);
		this.id = init+"("+id+")";
	}
	
	
	
	@Override
	public <T> T accept(SMVExprVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
