package jkind.smv.util;

import java.util.ArrayList;
import java.util.List;

import jkind.lustre.BinaryExpr;
import jkind.lustre.BinaryOp;
import jkind.lustre.BoolExpr;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.NamedType;
import jkind.lustre.Node;
import jkind.lustre.Program;
import jkind.lustre.VarDecl;
import jkind.lustre.builders.NodeBuilder;
import jkind.lustre.visitors.TypeAwareAstMapVisitor;

public class SMVRemoveArrow2 extends TypeAwareAstMapVisitor {

	public static Program program(Program p) {
		return new SMVRemoveArrow2().visit(p);
	}

	private List<VarDecl> newLocals = new ArrayList<>();
	private List<Equation> newEquations = new ArrayList<>();
	
	@Override
	public Node visit(Node e) {
		newEquations.add(new Equation(new IdExpr("init(initState)"), new BoolExpr(true)));
		newEquations.add(new Equation(new IdExpr("next(initState)"), new BoolExpr(false)));
		NodeBuilder builder = new NodeBuilder(super.visit(e));
		newLocals.add(new VarDecl("initState", NamedType.BOOL));
		builder.addLocals(newLocals);
		builder.addEquations(newEquations);
		return builder.build();
	}

	@Override
	public Expr visit(BinaryExpr be) {
		if (be.op == BinaryOp.ARROW) {
			return createTernaryExpr((be.left).accept(this), (be.right).accept(this));
		} else {
			return super.visit(be);
		}
	}

	private static Expr createTernaryExpr(Expr left, Expr right) {
		return new IdExpr("initState ? " + left.toString() + " : " + right.toString());
	}

}
