package jkind.smv.util;

import java.util.ArrayList;
import java.util.List;

import jkind.smv.SMVBinaryExpr;
import jkind.smv.SMVBinaryOp;
import jkind.smv.SMVBoolExpr;
import jkind.smv.SMVEquation;
import jkind.smv.SMVExpr;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVModule;
import jkind.smv.SMVNamedType;
import jkind.smv.SMVProgram;
import jkind.smv.SMVVarDecl;
import jkind.smv.builders.SMVModuleBuilder;
import jkind.smv.visitors.SMVTypeAwareAstMapVisitor;

public class SMVRemoveArrow3 extends SMVTypeAwareAstMapVisitor {

	public static SMVProgram program(SMVProgram p) {
		return new SMVRemoveArrow3().visit(p);
	}

	private List<SMVVarDecl> newLocals = new ArrayList<>();
	
	@Override
	public SMVModule visit(SMVModule e) {
		e.equations.add(new SMVEquation(new SMVIdExpr("init(initState)"), new SMVBoolExpr(true)));
		e.equations.add(new SMVEquation(new SMVIdExpr("next(initState)"), new SMVBoolExpr(false)));
		SMVModuleBuilder builder = new SMVModuleBuilder(super.visit(e));
		newLocals.add(new SMVVarDecl("initState", SMVNamedType.BOOL));
		builder.addLocals(newLocals);
		return builder.build();
	}

	@Override
	public SMVExpr visit(SMVBinaryExpr be) {
		if (be.op == SMVBinaryOp.ARROW) {
			return createTernaryExpr((be.left).accept(this), (be.right).accept(this));
		} else {
			return super.visit(be);
		}
	}

	private static SMVExpr createTernaryExpr(SMVExpr left, SMVExpr right) {
		return new SMVIdExpr("initState ? " + left.toString() + " : " + right.toString());
	}

}
