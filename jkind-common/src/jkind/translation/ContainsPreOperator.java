package jkind.translation;

import jkind.lustre.Expr;
import jkind.lustre.UnaryExpr;
import jkind.lustre.UnaryOp;
import jkind.lustre.visitors.ExprDisjunctiveVisitor;

/**
 * Check if an expression contains a 'pre'.
 */
public class ContainsPreOperator extends ExprDisjunctiveVisitor {
	public static boolean check(Expr e) {
		return e.accept(new ContainsPreOperator());
	}

	@Override
	public Boolean visit(UnaryExpr e) {
		return e.op == UnaryOp.PRE || super.visit(e);
	}
}
