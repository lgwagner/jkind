package jkind.translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jkind.lustre.BinaryExpr;
import jkind.lustre.BoolExpr;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.IntExpr;
import jkind.lustre.Node;
import jkind.lustre.Program;
import jkind.lustre.RealExpr;
import jkind.lustre.UnaryExpr;
import jkind.lustre.VarDecl;
import jkind.lustre.visitors.TypeAwareAstMapVisitor;
/*
 * flattening arrow operator ("->")
 * */
public class FlattenArrow extends TypeAwareAstMapVisitor{
	
	private List<VarDecl> newLocals = new ArrayList<>();
	private List<Equation> newEquations = new ArrayList<>();
	private int counter = 0;
	
	
	public FlattenArrow(Program program) {
		super.visit(program);
	}

	public Node node(Node node) {
		for (Equation eqn : node.equations) {
			if (ContainsArrowOperator.check(eqn.expr)) {
				Expr p = proc(eqn.expr);
				createEqn(eqn.lhs, getExpr(p));
			} else
				newEquations.add(eqn);
		}
		List<VarDecl> localsList = getLocals(node.locals);
		return new Node(node.location, node.id, node.inputs, node.outputs, getLocals(node.locals), newEquations, node.properties,
				node.assertions, node.realizabilityInputs, node.contract, node.ivc);
	}
	
	private List<VarDecl> getLocals(List<VarDecl> locals){
		List<VarDecl> tempLocals = new ArrayList<>();
		tempLocals.addAll(locals);
		for(VarDecl newLocalElement : newLocals ) {
			boolean elementExist = false;
			for( VarDecl varDecl : locals) {
				if(newLocalElement.id.equals(varDecl.id)) {
					elementExist = true;
				}
			}
			if(!elementExist) {
				tempLocals.add(newLocalElement);
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

	
	public Expr proc(Expr e) {
		if (e instanceof IdExpr) {
			IdExpr id = (IdExpr) e;
			return new IdExpr(id.toString());
		} else if (e instanceof UnaryExpr) {
			UnaryExpr ue = (UnaryExpr) e;
			return visitUnaryExpr(ue);
		}else if(e instanceof BoolExpr) {
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
			if (ue.expr instanceof IdExpr) {
				return createEqn(ue);
			}else {
				return new UnaryExpr(ue.op, proc(ue.expr));
			}

		} else
			return createEqn(ue);
	}

	private Expr visitBinaryExpr(BinaryExpr be) {
		if (ContainsArrowOperator.check(be)) {
			if(isTrivialExpr(be.left) & isTrivialExpr(be.right)){
			//if((be.left instanceof IdExpr | be.left instanceof BoolExpr | be.left instanceof IntExpr | be.left instanceof RealExpr) & (be.right instanceof IdExpr | be.right instanceof BoolExpr)) {
				return createEqn(be);
			}else {
				return visitBinaryExpr(new BinaryExpr(proc(be.left), be.op, proc(be.right)));
			}

		} else
			return createEqn(be);
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
	
	private Boolean isTrivialExpr(Expr e) {
		if(e instanceof IdExpr) {
			return true;
		}else if(e instanceof BoolExpr) {
			return true;
		}else if(e instanceof IntExpr) {
			return true;
		}else if(e instanceof RealExpr) {
			return true;
		}else {
			return false;
		}
		
	}

	private IdExpr createEqn(Expr expr) {
		List<IdExpr> lhs = new ArrayList<IdExpr>();
		lhs.add(new IdExpr(getFreshId().toString()));
		newEquations.add(new Equation(lhs, expr));
		newLocals.add(new VarDecl(lhs.get(0).id,super.getType(expr)));
		typeReconstructor.addVariable(new VarDecl(lhs.get(0).id,super.getType(expr)));
		return (IdExpr) lhs.get(0);
	}

	private IdExpr createEqn(List<IdExpr> lhs, Expr expr) {
		newEquations.add(new Equation(lhs, expr));
		newLocals.add(new VarDecl(lhs.get(0).id,super.getType(expr)));
		typeReconstructor.addVariable(new VarDecl(lhs.get(0).id,super.getType(expr)));
		return (IdExpr) lhs.get(0);
	}

	private IdExpr getFreshId() {
		return new IdExpr("Eqn_" + counter++);
	}

}
