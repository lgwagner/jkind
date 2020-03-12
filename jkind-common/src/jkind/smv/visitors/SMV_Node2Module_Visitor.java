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
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.IntExpr;
import jkind.lustre.NamedType;
import jkind.lustre.Node;
import jkind.lustre.SubrangeIntType;
import jkind.lustre.Type;
import jkind.lustre.UnaryExpr;
import jkind.lustre.UnaryOp;
import jkind.lustre.VarDecl;
import jkind.smv.SMVAst;
import jkind.smv.SMVBinaryExpr;
import jkind.smv.SMVBinaryOp;
import jkind.smv.SMVBoolExpr;
import jkind.smv.SMVCaseExpr;
import jkind.smv.SMVEquation;
import jkind.smv.SMVExpr;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVIntExpr;
import jkind.smv.SMVModule;
import jkind.smv.SMVNamedType;
import jkind.smv.SMVSubrangeIntType;
import jkind.smv.SMVType;
import jkind.smv.SMVUnaryExpr;
import jkind.smv.SMVUnaryOp;
import jkind.smv.SMVVarDecl;

public class SMV_Node2Module_Visitor implements SMV_Lus2SMV_Visitor<SMVAst, Ast> {

	@Override
	public SMVModule visit(Node node) {
		List<SMVVarDecl> sMVInputs = new ArrayList();
		List<SMVVarDecl> sMVOutputs = new ArrayList();
		List<SMVVarDecl> sMVlocals = new ArrayList();
		List<SMVEquation> sMVEquations = new ArrayList();
		List<String> sMVSpecifications = new ArrayList();

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
				sMVEquations.addAll(this.visit(eqn));
			}
		}
		if (!node.properties.isEmpty()) {
			for (String eqn : node.properties) {
				sMVSpecifications.add(eqn);
			}
		}
		return new SMVModule(node.id, sMVInputs, sMVOutputs, sMVlocals, sMVEquations, sMVSpecifications);
	}

	public SMVVarDecl visit(VarDecl vd) {
//		if(vd.type instanceof NamedType) {
		if (vd.type instanceof SubrangeIntType) {
			return new SMVVarDecl(vd.id, this.visitSubrangeIntType((SubrangeIntType) vd.type));
		}
		return new SMVVarDecl(vd.id, this.visit(vd.type));
//		}else {
//			return new SMVVarDecl(vd.id);
//		}
	}

	public SMVType visit(Type vd) {
		if (vd instanceof NamedType) {
			return visitNamedType((NamedType) vd);
		} else {
			return SMVNamedType.get(vd.toString());
		}
	}

	public SMVSubrangeIntType visitSubrangeIntType(SubrangeIntType vd) {
		return new SMVSubrangeIntType(vd.low, vd.high);
	}

	public SMVType visitNamedType(NamedType vd) {
		return SMVNamedType.get(vd.toString());
	}

	public List<SMVEquation> visit(Equation eqn) {
		List<SMVEquation> sMVEquation = new ArrayList<SMVEquation>();
		Expr expr = eqn.expr;
//		if (expr instanceof BinaryExpr) {
//			if(((BinaryExpr) expr).op.toString().contentEquals("->")) {
//				List<SMVIdExpr> lhs = new ArrayList<>();
//				for (IdExpr lh : eqn.lhs) {
//					lhs.add((SMVIdExpr) this.visit(lh));
//					sMVEquation.add(new SMVEquation(new SMVInitIdExpr(lh.id), this.visit(((BinaryExpr) expr).left)));
//					sMVEquation.add(new SMVEquation(new SMVNextIdExpr(lh.id), this.visit(((BinaryExpr) expr).right)));
//				}
//				return sMVEquation;
//			}
//		}
		List<SMVIdExpr> lhs = new ArrayList<>();
		for (IdExpr lh : eqn.lhs) {
			lhs.add((SMVIdExpr) this.visit(lh));
		}
		sMVEquation.add(new SMVEquation(lhs, this.visit(eqn.expr)));
		return sMVEquation;
	}

	public SMVExpr visit(Expr expr) {
		if (expr instanceof IdExpr) {
			return this.visitIdExpr((IdExpr) expr);
		}
		if (expr instanceof IntExpr) {
			return this.visitIntExpr((IntExpr) expr);
		}
		if (expr instanceof BoolExpr) {
			return this.visitBoolExpr((BoolExpr) expr);
		}
		if (expr instanceof UnaryExpr) {
			return this.visitUnaryExpr((UnaryExpr) expr);
		}
		if (expr instanceof BinaryExpr) {
			return this.visitBinaryExpr((BinaryExpr) expr);
		}
		if (expr instanceof IfThenElseExpr) {
			return this.visitIfThenElseExpr((IfThenElseExpr) expr);
		}
		return null;
	}

	public SMVEquation visitInitEqn(IdExpr idExpr, Expr expr) {
		List<SMVIdExpr> lhs = new ArrayList<>();
		lhs.add((SMVIdExpr) this.visit(idExpr));
		return new SMVEquation(lhs, this.visit(expr));
	}

	public SMVEquation visitNextEqn() {
		return null;

	}

	public SMVIdExpr visitIdExpr(IdExpr expr) {
		return new SMVIdExpr(expr.id);
	}

	public SMVIntExpr visitIntExpr(IntExpr expr) {
		return new SMVIntExpr(expr.value);
	}

	public SMVUnaryExpr visitUnaryExpr(UnaryExpr expr) {
		return new SMVUnaryExpr(this.visitUnaryOp(expr.op), this.visit(expr.expr));
	}

	public SMVUnaryOp visitUnaryOp(UnaryOp uo) {
		return SMVUnaryOp.fromString(uo.toString());
	}

	public SMVBoolExpr visitBoolExpr(BoolExpr expr) {
		return new SMVBoolExpr(expr.value);
	}

	public SMVBinaryExpr visitBinaryExpr(BinaryExpr expr) {
		if (expr.op.toString().contentEquals("->")) {

		}
		return new SMVBinaryExpr(this.visit(expr.left), visitExprOp(expr.op), this.visit(expr.right));
	}

	public SMVBinaryOp visitExprOp(BinaryOp op) {
		return SMVBinaryOp.fromString(op.toString());
	}

	public SMVCaseExpr visitIfThenElseExpr(IfThenElseExpr expr) {
		return new SMVCaseExpr(this.visit(expr.cond), this.visit(expr.thenExpr), this.visit(expr.elseExpr));
	}

}
