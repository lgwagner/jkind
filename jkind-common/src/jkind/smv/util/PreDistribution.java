package jkind.smv.util;

import jkind.lustre.BinaryExpr;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.Node;
import jkind.lustre.Program;
import jkind.lustre.UnaryExpr;
import jkind.lustre.UnaryOp;
import jkind.lustre.builders.NodeBuilder;
import jkind.lustre.visitors.TypeAwareAstMapVisitor;

public class PreDistribution extends TypeAwareAstMapVisitor {

	public static Program program(Program p) {
		return new PreDistribution().visit(p);
	}

	@Override
	public Node visit(Node e) {
		NodeBuilder builder = new NodeBuilder(super.visit(e));
		return builder.build();
	}
	
	

	@Override
	public Expr visit(UnaryExpr ue) {
		if (ue.op == UnaryOp.PRE) {
			if (ue.expr instanceof IdExpr) {
				return ue;
			} else if (ue.expr instanceof BinaryExpr){
				BinaryExpr be = (BinaryExpr) ue.expr;
				return new BinaryExpr(visit(new UnaryExpr(UnaryOp.PRE, be.left)), be.op, visit(new UnaryExpr(UnaryOp.PRE, be.right)));
			}else {
				return super.visit(ue);
			}
		} else {
			return super.visit(ue);
		}
	}

	
}
