package jkind.translation;

import java.util.ArrayList;
import java.util.List;

import jkind.lustre.BinaryExpr;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.Node;
import jkind.lustre.UnaryExpr;

public class FlattenArrow {

	private List<Equation> newEquations = new ArrayList<>();

	public Node node(Node node) {
		for (Equation eqn : node.equations) {
			if (ContainsArrowOperator.check(eqn.expr)) {
				Expr p = proc(eqn.expr);
				createEqn(eqn.lhs, getExpr(p));
			} else
				newEquations.add(eqn);
		}
		return new Node(node.location, node.id, node.inputs, node.outputs, node.locals, newEquations, node.properties,
				node.assertions, node.realizabilityInputs, node.contract, node.ivc);
	}

	private Expr getExpr(Expr expr) {
		for (Equation eqn : newEquations) {
			if (eqn.lhs.get(0).id.contentEquals(expr.toString())) {
				return eqn.expr;
			}
		}
		return expr;
	}

	private int counter = 0;

	public Expr proc(Expr e) {
		if (e instanceof IdExpr) {
			IdExpr id = (IdExpr) e;
			return new IdExpr(id.toString());
		} else if (e instanceof UnaryExpr) {
			UnaryExpr ue = (UnaryExpr) e;
			return new UnaryExpr(ue.op, proc(ue.expr));
		} else if (e instanceof BinaryExpr) {
			BinaryExpr be = (BinaryExpr) e;
			return visitBinaryExpr(be);
		} else if (e instanceof IfThenElseExpr) {
			IfThenElseExpr ite = (IfThenElseExpr) e;
			return visitIfThenElseExpr(ite);
		}

		return e;
	}

	private Expr visitBinaryExpr(BinaryExpr ue) {
		if (ContainsArrowOperator.check(ue)) {
			if (ue.left instanceof IdExpr & ue.right instanceof IdExpr) {
				return createEqn(ue);
			} else {
				return visitBinaryExpr(new BinaryExpr(proc(ue.left), ue.op, proc(ue.right)));
			}

		} else
			return createEqn(ue);
	}

	private Expr visitIfThenElseExpr(IfThenElseExpr ite) {
		if (ContainsArrowOperator.check(ite)) {
			if (ite.cond instanceof IdExpr & ite.thenExpr instanceof IdExpr & ite.elseExpr instanceof IdExpr) {
				return createEqn(ite);
			} else {
				return visitIfThenElseExpr(new IfThenElseExpr(proc(ite.cond), proc(ite.thenExpr), proc(ite.elseExpr)));
			}
		} else
			return createEqn(ite);
	}

	private IdExpr createEqn(Expr expr) {
		List<IdExpr> lhs = new ArrayList<IdExpr>();
		lhs.add(new IdExpr(getFreshId().toString()));
		newEquations.add(new Equation(lhs, expr));
		return (IdExpr) lhs.get(0);
	}

	private IdExpr createEqn(List<IdExpr> lhs, Expr expr) {
		newEquations.add(new Equation(lhs, expr));
		return (IdExpr) lhs.get(0);
	}

	private IdExpr getFreshId() {
		return new IdExpr("Eqn_" + counter++);
	}

}
