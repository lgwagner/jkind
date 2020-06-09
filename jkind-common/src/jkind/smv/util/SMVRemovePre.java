package jkind.smv.util;

import java.util.ArrayList;
import java.util.List;

import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.Node;
import jkind.lustre.Program;
import jkind.lustre.UnaryExpr;
import jkind.lustre.UnaryOp;
import jkind.lustre.VarDecl;
import jkind.lustre.builders.NodeBuilder;
import jkind.lustre.visitors.TypeAwareAstMapVisitor;

public class SMVRemovePre extends TypeAwareAstMapVisitor {

	public static Program program(Program p) {
		return new SMVRemovePre().visit(p);
	}

	private List<VarDecl> newLocals = new ArrayList<>();
	private List<Equation> newEquations = new ArrayList<>();
	private List<String> preVarList = new ArrayList<>();
	private int counter = 0;

	@Override
	public Node visit(Node e) {
		NodeBuilder builder = new NodeBuilder(super.visit(e));
		builder.addLocals(removeDuplicateLocals(e.inputs, e.outputs, e.locals));
		builder.addEquations(newEquations);
		return builder.build();
	}

	private Expr process(Expr original) {
		Expr nested = original.accept(this);
		IdExpr id = getFreshId();
		newLocals.add(new VarDecl(id.id, getType(original)));
		newEquations.add(new Equation(id, nested));
		return id;
	}

	private IdExpr getFreshId() {
		return new IdExpr("~smashedPre" + counter++);
	}

	private IdExpr concatNext(Expr e) {
		if (e instanceof IdExpr) {
			IdExpr ide = (IdExpr) e;
			return new IdExpr("next(" + ide.id + ")");
		} else {
			throw new IllegalArgumentException("illegal arg : " + e.toString());
		}
	}

	@Override
	public Expr visit(UnaryExpr ue) {
		if (ue.op == UnaryOp.PRE) {
			if (ue.expr instanceof IdExpr) {
				return createNextEqn((IdExpr) ue.expr);
			} else {
				return process(ue.expr);
			}
		} else {
			return super.visit(ue);
		}
	}

	private IdExpr concatPre(Expr e) {
		if (e instanceof IdExpr) {
			IdExpr ide = (IdExpr) e;
			return new IdExpr("~pre_" + ide.id);
		} else {
			throw new IllegalArgumentException("illegal arg : " + e.toString());
		}
	}

	private IdExpr createNextEqn(IdExpr expr) {
		IdExpr ie = concatPre(expr);
		String preExpr = ie.id;
		if (!preVarList.contains(preExpr)) {
			newLocals.add(new VarDecl(preExpr, super.getType(expr)));
			newEquations.add(new Equation(concatNext(ie), expr));
			typeReconstructor.addVariable(new VarDecl(preExpr, super.getType(expr)));
			preVarList.add(preExpr);
		}
		return new IdExpr(preExpr);

	}

	private List<VarDecl> removeDuplicateLocals(List<VarDecl> inputs, List<VarDecl> outputs, List<VarDecl> locals) {
		List<VarDecl> tempLocals = new ArrayList<>();
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

}
