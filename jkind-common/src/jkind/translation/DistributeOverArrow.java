package jkind.translation;

import java.util.ArrayList;
import java.util.List;

import jkind.lustre.BinaryExpr;
import jkind.lustre.BoolExpr;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.IntExpr;
import jkind.lustre.Location;
import jkind.lustre.Node;
import jkind.lustre.RealExpr;
import jkind.lustre.UnaryExpr;
import jkind.lustre.builders.NodeBuilder;
import jkind.lustre.visitors.AstMapVisitor;

public class DistributeOverArrow {

	private List<Equation> newEquations = new ArrayList<>();

	
	
	public Node node(Node node) {
		for (Equation eqn : node.equations) {
			proc(eqn);
			newEquations.add(eqnProc(eqn));
		}
		//node.
		//node.equations.removeAll(equations);
		//node.equations.addAll(this.newEquations);
		return new Node(node.location,node.id,node.inputs,node.outputs,node.locals,newEquations,node.properties,
				node.assertions,node.realizabilityInputs,node.contract,node.ivc);
	}

	private int counter = 0;
	
	public Equation proc(Equation eq) {
		if(eq.expr instanceof UnaryExpr) {
			UnaryExpr ue = (UnaryExpr) eq.expr;
		}
		if(eq.expr instanceof BinaryExpr) {
			BinaryExpr ue = (BinaryExpr) eq.expr;
		}
		
		if(ContainsArrowOperator.check(eq.expr)) {
			if(eq.expr instanceof BinaryExpr) {
				BinaryExpr be = (BinaryExpr) eq.expr;
				if(ContainsArrowOperator.check(be.left)) {
					List newlhs = new ArrayList<IdExpr>();
					for(IdExpr id : eq.lhs) {
						newlhs.add(new IdExpr(id.toString()+getFreshId()));
					}
					newEquations.add(new Equation(newlhs,be.left));
				}
			}
			
		}
		return null;
	}

	private Expr lift(BinaryExpr expr) {
		if(ContainsArrowOperator.check(expr.left)) {
			
		}
		if(ContainsArrowOperator.check(expr.right)) {
			
		}
		
		return expr;
		
	}
	
	private IdExpr getFreshId() {
		return new IdExpr("_" + counter++);
	}

	public Equation eqnProc(Equation eq) {
		return new Equation(eq.lhs, fun(eq.expr));
	}

	public Expr fun(Expr expr) {
		if (expr instanceof IdExpr) {
			return expr;
		}
		if (expr instanceof IntExpr) {
			return expr;
		}
		if (expr instanceof RealExpr) {
			return expr;
		}
		if (expr instanceof BoolExpr) {
			return expr;
		}
		if (expr instanceof UnaryExpr) {
			return unaryExprProc((UnaryExpr) expr);
		}
		if (expr instanceof BinaryExpr) {
			return binaryExprProc((BinaryExpr) expr);

		}
		if (expr instanceof IfThenElseExpr) {
			return new IfThenElseExpr(fun(((IfThenElseExpr) expr).cond), fun(((IfThenElseExpr) expr).thenExpr),
					fun(((IfThenElseExpr) expr).elseExpr));

		} else {
			return expr;
		}

	}

	public Expr booleanExprProc(BoolExpr e) {
		return e;
	}

	public Expr unaryExprProc(UnaryExpr e) {
		if(e.expr instanceof BinaryExpr) {
			BinaryExpr be = (BinaryExpr) e.expr;
			return new BinaryExpr(new UnaryExpr(e.op,fun(be.left)),be.op,new UnaryExpr(e.op,fun(be.right)));
		}
		return new UnaryExpr(e.op, fun(e.expr));
	}

	public Expr binaryExprProc(BinaryExpr expr) {
		if (expr.left instanceof BinaryExpr) {
			BinaryExpr s_left = (BinaryExpr) expr.left;
			if (s_left.op.toString().contains("->")) {
				BinaryExpr left = new BinaryExpr(s_left.left, expr.op, expr.right);
				BinaryExpr right = new BinaryExpr(s_left.right, expr.op, expr.right);
				return new BinaryExpr(left, s_left.op, right);
			}
		}
		if (expr.right instanceof BinaryExpr) {
			BinaryExpr s_right = (BinaryExpr) expr.right;
			if (s_right.op.toString().contains("->")) {
				BinaryExpr left = new BinaryExpr(expr.left, expr.op, s_right.left);
				BinaryExpr right = new BinaryExpr(expr.left, expr.op, s_right.right);
				return new BinaryExpr(left, s_right.op, right);
			}
		}
		return expr;
	}
	
	

}
