package jkind.smv.util;

import java.util.ArrayList;
import java.util.List;

import jkind.lustre.BinaryExpr;
import jkind.lustre.BoolExpr;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.Node;
import jkind.lustre.Program;
import jkind.lustre.UnaryExpr;
import jkind.lustre.VarDecl;
import jkind.lustre.visitors.TypeAwareAstMapVisitor;
import jkind.translation.ContainsArrowOperator;

/*
 * flattening arrow operator ("->")
 * */
//TODO some functions are same as in SMVFlattenPre, consider having only one copy 
public class SMVFlattenArrow extends TypeAwareAstMapVisitor {

	private List<VarDecl> newLocals = new ArrayList<>();
	private List<Equation> newEquations = new ArrayList<>();

	private int counter = 0;

	public SMVFlattenArrow(Program program) {
		super.visit(program);
	}

	public Node node(Node node) {
		for (Equation eqn : node.equations) {
			if (ContainsArrowOperator.check(eqn.expr)) {
				createEqn(eqn.lhs, proc(eqn.expr));
			} else
				newEquations.add(eqn);
		}
		return new Node(node.location, node.id, node.inputs, node.outputs,
				getLocals(node.inputs, node.outputs, node.locals), newEquations, node.properties, node.assertions,
				node.realizabilityInputs, node.contract, node.ivc);
	}

	private List<VarDecl> getLocals(List<VarDecl> inputs, List<VarDecl> outputs, List<VarDecl> locals) {
		List<VarDecl> tempLocals = new ArrayList<>();
		tempLocals.addAll(locals);
		for (VarDecl newLocalVar : newLocals) {
			boolean elementExist = false;
			for (VarDecl varDecl : inputs) {
				if (newLocalVar.id.equals(varDecl.id)) {
					elementExist = true;
					break;
				}
			}
			if (!elementExist) {
				for (VarDecl varDecl : outputs) {
					if (newLocalVar.id.equals(varDecl.id)) {
						elementExist = true;
						break;
					}
				}
			}
			if (!elementExist) {
				for (VarDecl varDecl : locals) {
					if (newLocalVar.id.equals(varDecl.id)) {
						elementExist = true;
						break;
					}
				}
			}
			if (!elementExist) {
				tempLocals.add(newLocalVar);
			}
		}
		return tempLocals;
	}

	// TODO consider removing this
//	private Expr getExpr(Expr expr) {
//		for (Equation eqn : newEquations) {
//			if (eqn.lhs.get(0).id.contentEquals(expr.toString())) {
//				return eqn.expr;
//			}
//		}
//		return expr;
//	}

	// TODO why to create new IdExpr, can return the original one itself
	public Expr proc(Expr e) {
		if (e instanceof IdExpr) {
			IdExpr id = (IdExpr) e;
			return new IdExpr(id.toString());
		} else if (e instanceof UnaryExpr) {
			UnaryExpr ue = (UnaryExpr) e;
			return visitUnaryExpr(ue);
		} else if (e instanceof BoolExpr) {
			BoolExpr be = (BoolExpr) e;
			return new BoolExpr(be.value);
		} else if (e instanceof BinaryExpr) {
			BinaryExpr be = (BinaryExpr) e;
			return visitBinaryExpr(be);
		} else if (e instanceof IfThenElseExpr) {
			IfThenElseExpr ite = (IfThenElseExpr) e;
			return visitIfThenElseExpr(ite);
		}
		return e;
	}

	private Expr visitUnaryExpr(UnaryExpr ue) {
		if (ContainsArrowOperator.check(ue)) {
			return new UnaryExpr(ue.op, proc(ue.expr));
		} else
			return createEqn(ue);
	}

	private Expr visitBinaryExpr(BinaryExpr be) {
		if (ContainsArrowOperator.check(be)) {
			if ((!ContainsArrowOperator.check(be.left)) & (!ContainsArrowOperator.check(be.right))) {
				return createEqn(be);
			} else {
				return visitBinaryExpr(new BinaryExpr(proc(be.left), be.op, proc(be.right)));
			}

		} else
			return be;
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
		newLocals.add(new VarDecl(lhs.get(0).id, super.getType(expr)));
		typeReconstructor.addVariable(new VarDecl(lhs.get(0).id, super.getType(expr)));
		return (IdExpr) lhs.get(0);
	}

	private IdExpr createEqn(List<IdExpr> lhs, Expr expr) {
		newEquations.add(new Equation(lhs, expr));
		newLocals.add(new VarDecl(lhs.get(0).id, super.getType(expr)));
		typeReconstructor.addVariable(new VarDecl(lhs.get(0).id, super.getType(expr)));
		return (IdExpr) lhs.get(0);
	}

	private IdExpr getFreshId() {
		return new IdExpr("Eqn_" + counter++);
	}

}
