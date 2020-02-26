package jkind.smv.visitors;

import java.util.ArrayList;
import java.util.List;

import jkind.lustre.Ast;
import jkind.lustre.BinaryExpr;
import jkind.lustre.BinaryOp;
import jkind.lustre.BoolExpr;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.IntExpr;
import jkind.lustre.NamedType;
import jkind.lustre.Node;
import jkind.lustre.Type;
import jkind.lustre.VarDecl;
import jkind.smv.SMVModule;
import jkind.smv.SMVAst;
import jkind.smv.SMVBinaryExpr;
import jkind.smv.SMVBinaryOp;
import jkind.smv.SMVBoolExpr;
import jkind.smv.SMVEquation;
import jkind.smv.SMVExpr;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVIntExpr;
import jkind.smv.SMVNamedType;
import jkind.smv.SMVType;
import jkind.smv.SMVUnaryExpr;
import jkind.smv.SMVVarDecl;

public class SMV_Node2Module_Visitor implements SMV_Lus2SMV_Visitor<SMVAst, Ast> {

	@Override
	public SMVModule visit(Node node) {
		List<SMVVarDecl> sMVInputs = new ArrayList();
		List<SMVVarDecl> sMVOutputs = new ArrayList();
		List<SMVVarDecl> sMVlocals = new ArrayList();
		List<SMVEquation> sMVEquations = new ArrayList();
		if (!node.inputs.isEmpty()) {
			for (VarDecl input : node.inputs) {
				sMVInputs.add(this.visit(input));
			}
		}
		if (!node.outputs.isEmpty()) {
			for (VarDecl output : node.outputs) {
				sMVOutputs.add(this.visit(output));
			}
		}
		if (!node.locals.isEmpty()) {
			for (VarDecl local : node.locals) {
				sMVlocals.add(this.visit(local));
			}
		}
		if (!node.equations.isEmpty()) {
			for (Equation eqn : node.equations) {
				sMVEquations.add(this.visit(eqn));
			}
		}
		return new SMVModule(node.id, sMVInputs, sMVOutputs, sMVlocals, sMVEquations);
	}

	public SMVVarDecl visit(VarDecl vd) {
//		if(vd.type instanceof NamedType) {
		return new SMVVarDecl(vd.id, this.visit(vd.type));
//		}else {
//			return new SMVVarDecl(vd.id);
//		}
	}

	public SMVType visit(Type vd) {
		if (vd instanceof NamedType) {
			return visitNamedType((NamedType) vd);
		} else {
			return new SMVNamedType(vd.toString());
		}
	}

	public SMVType visitNamedType(NamedType vd) {
		return new SMVNamedType(vd.toString());
	}

	public SMVEquation visit(Equation eqn) {
		SMVExpr ae = visit(eqn.expr);
		List<SMVIdExpr> lhs = new ArrayList<>();
		for(IdExpr lh : eqn.lhs) {
			lhs.add((SMVIdExpr) this.visit(lh));
		}
		return new SMVEquation(lhs, this.visit(eqn.expr));
	}

	public SMVExpr visit(Expr expr) {
		if (expr instanceof IdExpr) {
			return this.visitIdExpr((IdExpr) expr);
		}
		if (expr instanceof IntExpr) {
			return this.visitIntExpr((IntExpr) expr);
		}
		if (expr instanceof BinaryExpr) {
			return this.visitBinaryExpr((BinaryExpr) expr);
		}
		if(expr instanceof BoolExpr) {
			return this.visitBoolExpr((BoolExpr)expr);
		}
		return null;
	}

	public SMVIdExpr visitIdExpr(IdExpr expr) {
		SMVIdExpr sie = new SMVIdExpr(expr.id);
		// sie.accept(this);
		return new SMVIdExpr(expr.id);
	}

	public SMVIntExpr visitIntExpr(IntExpr expr) {
		return new SMVIntExpr(expr.value);
	}

	public SMVBoolExpr visitBoolExpr(BoolExpr expr) {
		return new SMVBoolExpr(expr.value);
	}
	
	public SMVBinaryExpr visitBinaryExpr(BinaryExpr expr) {
		return new SMVBinaryExpr(this.visit(expr.left), visitExprOp(expr.op), this.visit(expr.right));
	}

	public SMVBinaryOp visitExprOp(BinaryOp op) {
		return SMVBinaryOp.fromString(op.toString());
	}

//	@Override
//	public SMVAst visit(SMVBinaryExpr e) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//	@Override
//	public SMVAst visit(SMVBoolExpr e) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//	@Override
//	public SMVAst visit(SMVIdExpr e) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//	@Override
//	public SMVAst visit(SMVIntExpr e) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//
//
//	@Override
//	public SMVAst visit(SMVUnaryExpr e) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}