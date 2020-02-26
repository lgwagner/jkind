package jkind.smv;

import jkind.smv.visitors.SMVAstVisitor;
import jkind.smv.visitors.SMVExprVisitor;

public abstract class SMVExpr extends SMVAst{

	
	@SuppressWarnings("unchecked")
	@Override
	public <T, S extends T> S accept(SMVAstVisitor<T, S> visitor) {
		return accept((SMVExprVisitor<S>) visitor);
	}
	
	public abstract <T> T accept(SMVExprVisitor<T> visitor);
	
}
