package jkind.smv.util;

import java.util.ArrayList;
import java.util.List;

import jkind.lustre.BinaryExpr;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.Node;
import jkind.lustre.Program;
import jkind.lustre.UnaryExpr;
import jkind.lustre.VarDecl;
import jkind.lustre.visitors.TypeAwareAstMapVisitor;
import jkind.translation.ContainsPreOperator;

public class SMVPreDistribution extends TypeAwareAstMapVisitor {

	private List<String> nodeVarList = new ArrayList<>();

	private List<VarDecl> newLocals = new ArrayList<>();
	private List<Equation> newEquations = new ArrayList<>();
	private List<String> preVarList = new ArrayList<>();
	private List<IdExpr> eqnNames = new ArrayList<>();

	private int counter = 0;

	public SMVPreDistribution(Program program) {
		super.visit(program);
	}

	public Node node(Node node) {
		createNodeVareList(node.inputs, node.outputs, node.locals);
		for (Equation eqn : node.equations) {
			eqnNames.addAll(eqn.lhs);
			if (ContainsPreOperator.check(eqn.expr)) {
				Expr p = proc(eqn.expr);
				createEqn(eqn.lhs, getExpr(p));
			} else {
				newEquations.add(eqn);
			}
			eqnNames.clear();
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

	private Expr getExpr(Expr expr) {
		for (Equation eqn : newEquations) {
			if (eqn.lhs.get(0).id.contentEquals(expr.toString())) {
				return eqn.expr;
			}
		}
		return expr;
	}

	// TODO make if block one liner
	public Expr proc(Expr e) {
		if (e instanceof IdExpr) {
			IdExpr id = (IdExpr) e;
			return new IdExpr(id.toString());
		} else if (e instanceof UnaryExpr) {
			UnaryExpr ue = (UnaryExpr) e;
			return visitUnaryExpr(ue);
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
		if (ContainsPreOperator.check(ue)) {
			if (ue.expr instanceof IdExpr) {
				return createEqnPreId((IdExpr) ue.expr);
			} else if (!ContainsPreOperator.check(ue.expr)) {
				return proc(new UnaryExpr(ue.op, createEqnPreId(ue.expr)));
			} else {
				return new UnaryExpr(ue.op, proc(ue.expr));
			}
		} else
			return new UnaryExpr(ue.op, proc(ue.expr));
	}

	private Expr visitBinaryExpr(BinaryExpr be) {
		if (ContainsPreOperator.check(be)) {
			/* TODO following if block does not make sense as if "be" containes pre means
			 either left has pre or right has pre. Find the situation where it can be violated. */
			if (!ContainsPreOperator.check(be.left) & !ContainsPreOperator.check(be.right)) {
				return be;
			} else {
				return visitBinaryExpr(new BinaryExpr(proc(be.left), be.op, proc(be.right)));
			}
		} else
			return be;
	}

	private Expr visitIfThenElseExpr(IfThenElseExpr ite) {
		if (ContainsPreOperator.check(ite)) {
			if (ite.cond instanceof IdExpr & ite.thenExpr instanceof IdExpr & ite.elseExpr instanceof IdExpr) {
				return ite;
			} else {
				return visitIfThenElseExpr(new IfThenElseExpr(proc(ite.cond), proc(ite.thenExpr), proc(ite.elseExpr)));
			}
		} else
			return ite;
	}

	private IdExpr createEqnPreId(Expr expr) {
		List<IdExpr> lhs = new ArrayList<>();
		lhs.add(getFreshId());
		return createEqn(lhs, expr);
	}

	private IdExpr createEqnPreId(IdExpr expr) {
		// TODO: write the logic behind this
		if (!isSelfReference(expr)) {
			List<IdExpr> lhs = new ArrayList<IdExpr>();
			/* concatenating 'pre' */
			IdExpr ie = concatPre(expr);
			/* concatenating 'next' */
			lhs.add(concatNext(ie));
			String preExpr = ie.id;
			if (!preVarList.contains(preExpr)) {
				newEquations.add(new Equation(lhs, expr));
				newLocals.add(new VarDecl(preExpr, super.getType(expr)));
				typeReconstructor.addVariable(new VarDecl(preExpr, super.getType(expr)));
				preVarList.add(preExpr);
			}
			return new IdExpr(preExpr);
		}
		return expr;
	}

	private IdExpr createEqn(List<IdExpr> lhs, Expr expr) {
		newEquations.add(new Equation(lhs, expr));
		newLocals.add(new VarDecl(lhs.get(0).id, super.getType(expr)));
		typeReconstructor.addVariable(new VarDecl(lhs.get(0).id, super.getType(expr)));
		return (IdExpr) lhs.get(0);
	}

	private IdExpr concatPre(Expr e) {
		if (e instanceof IdExpr) {
			IdExpr ide = (IdExpr) e;
			if (nodeVarList.contains("pre_" + ide.id)) {
				return new IdExpr("pre_" + ide.id + "N");
			} else {
				return new IdExpr("pre_" + ide.id);
			}
		}
		throw new IllegalArgumentException("illegal arg : " + e.toString());

	}

	private IdExpr concatNext(Expr e) {
		if (e instanceof IdExpr) {
			IdExpr ide = (IdExpr) e;
			return new IdExpr("next(" + ide.id + ")");
		}
		throw new IllegalArgumentException("illegal arg : " + e.toString());
	}

	private void createNodeVareList(List<VarDecl> inputs, List<VarDecl> outputs, List<VarDecl> locals) {
		for (VarDecl ip : inputs) {
			nodeVarList.add(ip.id);
		}
		for (VarDecl op : outputs) {
			nodeVarList.add(op.id);
		}
		for (VarDecl local : locals) {
			nodeVarList.add(local.id);
		}

	}

	private boolean isSelfReference(IdExpr e) {
		for (IdExpr ie : eqnNames) {
			if (ie.id.contentEquals(e.id)) {
				return true;
			}
		}
		return false;
	}

	private IdExpr getFreshId() {
		return new IdExpr("preEqn_" + counter++);
	}

}
