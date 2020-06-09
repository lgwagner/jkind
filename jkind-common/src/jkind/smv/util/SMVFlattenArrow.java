package jkind.smv.util;

import java.util.ArrayList;
import java.util.List;

import jkind.lustre.BinaryExpr;
import jkind.lustre.BinaryOp;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.Node;
import jkind.lustre.Program;
import jkind.lustre.UnaryExpr;
import jkind.lustre.VarDecl;
import jkind.lustre.builders.NodeBuilder;
import jkind.lustre.visitors.TypeAwareAstMapVisitor;
import jkind.translation.ContainsArrowOperator;

public class SMVFlattenArrow extends TypeAwareAstMapVisitor {

	public static Program program(Program p) {
		return new SMVFlattenArrow().visit(p);
	}

	private List<VarDecl> newLocals = new ArrayList<>();
	private List<Equation> newEquations = new ArrayList<>();
	private int counter = 0;

	@Override
	public Node visit(Node e) {
		NodeBuilder builder = new NodeBuilder(super.visit(e));
		builder.addLocals(newLocals);
		builder.addEquations(newEquations);
		return builder.build();
	}

	private IdExpr getFreshId() {
		return new IdExpr("~smashed" + counter++);
	}

	private Expr process(Expr original) {
		Expr nested = original.accept(this);
		IdExpr id = getFreshId();
		newLocals.add(new VarDecl(id.id, getType(original)));
		newEquations.add(new Equation(id, nested));
		return id;
	}

	@Override
	public Expr visit(UnaryExpr ue) {
		if (ContainsArrowOperator.check(ue)) {
			return new UnaryExpr(ue.op, process(ue.expr));
		} else {
			return super.visit(ue);
		}
	}

	@Override
	public Expr visit(BinaryExpr be) {

		if (be.op == BinaryOp.ARROW) {
			Expr newLeft = be.left;
			Expr newRight = be.right;

			if (ContainsArrowOperator.check(be.left)) {
				newLeft = process(be.left);
			}

			if (ContainsArrowOperator.check(be.right)) {
				newRight = process(be.right);
			}

			return new BinaryExpr(newLeft, BinaryOp.ARROW, newRight);
		} else {
			return super.visit(be);
		}
	}

	@Override
	public Expr visit(IfThenElseExpr ite) {
		if (ContainsArrowOperator.check(ite)) {
			Expr newCond = ite.cond;
			Expr newThen = ite.thenExpr;
			Expr newElse = ite.elseExpr;
			if (ContainsArrowOperator.check(ite.cond)) {
				newCond = process(ite.cond);
			}
			if (ContainsArrowOperator.check(ite.thenExpr)) {
				newThen = process(ite.thenExpr);
			}
			if (ContainsArrowOperator.check(ite.elseExpr)) {
				newElse = process(ite.elseExpr);
			}
			return new IfThenElseExpr(newCond, newThen, newElse);
		} else {
			return super.visit(ite);
		}
	}
}