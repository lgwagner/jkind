package jkind.smv.visitors;

import java.util.ArrayList;
import java.util.List;

import jkind.lustre.Ast;
import jkind.lustre.BinaryExpr;
import jkind.lustre.BinaryOp;
import jkind.lustre.BoolExpr;
import jkind.lustre.CastExpr;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.IntExpr;
import jkind.lustre.NamedType;
import jkind.lustre.Node;
import jkind.lustre.RealExpr;
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
import jkind.smv.SMVCastExpr;
import jkind.smv.SMVEquation;
import jkind.smv.SMVExpr;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVIntExpr;
import jkind.smv.SMVModule;
import jkind.smv.SMVNamedType;
import jkind.smv.SMVRealExpr;
import jkind.smv.SMVSubrangeIntType;
import jkind.smv.SMVType;
import jkind.smv.SMVUnaryExpr;
import jkind.smv.SMVUnaryOp;
import jkind.smv.SMVVarDecl;

public class SMV_Node2Module_Visitor implements SMV_Lus2SMV_Visitor<SMVAst, Ast> {

	@Override
	public SMVModule visit(Node node) {
		List<SMVVarDecl> sMVInputs = new ArrayList<SMVVarDecl>();
		List<SMVVarDecl> sMVOutputs = new ArrayList<SMVVarDecl>();
		List<SMVVarDecl> sMVlocals = new ArrayList<SMVVarDecl>();
		List<SMVEquation> sMVEquations = new ArrayList<SMVEquation>();
		List<String> sMVSpecifications = new ArrayList<String>();

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
		return new SMVVarDecl(vd.id, this.visit(vd.type));
	}

	public SMVType visit(Type type) {
		if (type instanceof NamedType) {
			return visit((NamedType) type);
		} else if (type instanceof SubrangeIntType) {
			return visit((SubrangeIntType) type);
		} else {
			return SMVNamedType.get(type.toString());
		}
	}

	public SMVType visit(NamedType vd) {
		return SMVNamedType.get(vd.toString());
	}

	public SMVSubrangeIntType visit(SubrangeIntType vd) {
		return new SMVSubrangeIntType(vd.low, vd.high);
	}

	public List<SMVEquation> visit(Equation eqn) {
		List<SMVEquation> sMVEquation = new ArrayList<SMVEquation>();
		List<SMVIdExpr> lhs = new ArrayList<>();
		for (IdExpr lh : eqn.lhs) {
			lhs.add((SMVIdExpr) this.visit(lh));
		}
		sMVEquation.add(new SMVEquation(lhs, this.visit(eqn.expr)));
		return sMVEquation;
	}

	public SMVExpr visit(Expr expr) {
		if (expr instanceof IdExpr) {
			return this.visit((IdExpr) expr);
		}
		if (expr instanceof IntExpr) {
			return this.visit((IntExpr) expr);
		}
		if (expr instanceof RealExpr) {
			return this.visit((RealExpr) expr);
		}
		if (expr instanceof BoolExpr) {
			return this.visit((BoolExpr) expr);
		}
		if (expr instanceof UnaryExpr) {
			return this.visit((UnaryExpr) expr);
		}
		if (expr instanceof BinaryExpr) {
			return this.visit((BinaryExpr) expr);
		}
		if (expr instanceof IfThenElseExpr) {
			return this.visit((IfThenElseExpr) expr);
		}
		if (expr instanceof CastExpr) {
			return this.visit((CastExpr) expr);
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

	public SMVIdExpr visit(IdExpr expr) {
		return new SMVIdExpr(expr.id);
	}

	public SMVIntExpr visit(IntExpr expr) {
		return new SMVIntExpr(expr.value);
	}

	public SMVRealExpr visit(RealExpr expr) {
		return new SMVRealExpr(expr.value);
	}

	public SMVUnaryExpr visit(UnaryExpr expr) {
		return new SMVUnaryExpr(this.visitUnaryOp(expr.op), this.visit(expr.expr));
	}

	public SMVUnaryOp visitUnaryOp(UnaryOp uo) {
		return SMVUnaryOp.fromString(uo.toString());
	}

	public SMVBoolExpr visit(BoolExpr expr) {
		return new SMVBoolExpr(expr.value);
	}

	public SMVBinaryExpr visit(BinaryExpr expr) {
		return new SMVBinaryExpr(this.visit(expr.left), visitBinaryOp(expr.op), this.visit(expr.right));
	}

	public SMVBinaryOp visitBinaryOp(BinaryOp op) {
		return SMVBinaryOp.fromString(op.toString());
	}

	public SMVCaseExpr visit(IfThenElseExpr expr) {
		return new SMVCaseExpr(this.visit(expr.cond), this.visit(expr.thenExpr), this.visit(expr.elseExpr));
	}
	
	public SMVCastExpr visit(CastExpr expr) {
		return new SMVCastExpr(this.visit(expr.type), this.visit(expr.expr));
	}

}
