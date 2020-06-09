package jkind.smv.visitors;

import java.util.ArrayList;
import java.util.List;

import jkind.lustre.ArrayAccessExpr;
import jkind.lustre.ArrayExpr;
import jkind.lustre.ArrayUpdateExpr;
import jkind.lustre.BinaryExpr;
import jkind.lustre.BinaryOp;
import jkind.lustre.BoolExpr;
import jkind.lustre.CastExpr;
import jkind.lustre.CondactExpr;
import jkind.lustre.Constant;
import jkind.lustre.Contract;
import jkind.lustre.Equation;
import jkind.lustre.Expr;
import jkind.lustre.Function;
import jkind.lustre.FunctionCallExpr;
import jkind.lustre.IdExpr;
import jkind.lustre.IfThenElseExpr;
import jkind.lustre.IntExpr;
import jkind.lustre.NamedType;
import jkind.lustre.Node;
import jkind.lustre.NodeCallExpr;
import jkind.lustre.Program;
import jkind.lustre.RealExpr;
import jkind.lustre.RecordAccessExpr;
import jkind.lustre.RecordExpr;
import jkind.lustre.RecordUpdateExpr;
import jkind.lustre.SubrangeIntType;
import jkind.lustre.TupleExpr;
import jkind.lustre.Type;
import jkind.lustre.TypeDef;
import jkind.lustre.UnaryExpr;
import jkind.lustre.UnaryOp;
import jkind.lustre.VarDecl;
import jkind.lustre.visitors.AstVisitor;
import jkind.smv.SMVArrayExpr;
import jkind.smv.SMVAst;
import jkind.smv.SMVBinaryExpr;
import jkind.smv.SMVBinaryOp;
import jkind.smv.SMVBoolExpr;
import jkind.smv.SMVCaseExpr;
import jkind.smv.SMVCastExpr;
import jkind.smv.SMVEquation;
import jkind.smv.SMVExpr;
import jkind.smv.SMVFunction;
import jkind.smv.SMVFunctionCallExpr;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVIntExpr;
import jkind.smv.SMVModule;
import jkind.smv.SMVNamedType;
import jkind.smv.SMVProgram;
import jkind.smv.SMVRealExpr;
import jkind.smv.SMVSubrangeIntType;
import jkind.smv.SMVType;
import jkind.smv.SMVUnaryExpr;
import jkind.smv.SMVUnaryOp;
import jkind.smv.SMVVarDecl;
import jkind.smv.util.Lustre2SMVException;
import jkind.smv.util.SMVValidId;

public class SMVLustre2SMVVisitor implements AstVisitor<SMVAst, SMVAst> {

	public static SMVProgram program(Program p) {
		return new SMVLustre2SMVVisitor().visit(p);
	}

	@Override
	public SMVAst visit(Constant constant) {
		throw new Lustre2SMVException("Constant is not expected at this point.");
	}

	@Override
	public SMVAst visit(TypeDef typeDef) {
		throw new Lustre2SMVException("TypeDef is not expected at this point.");
	}

	@Override
	public SMVAst visit(Contract contract) {
		throw new Lustre2SMVException("Contract is not expected at this point.");
	}

	@Override
	public SMVFunction visit(Function function) {
		List<SMVVarDecl> smvInputs = new ArrayList<SMVVarDecl>();
		List<SMVVarDecl> smvOutputs = new ArrayList<SMVVarDecl>();
		for (VarDecl input : function.inputs) {
			smvInputs.add(this.visit(input));
		}
		for (VarDecl output : function.outputs) {
			smvOutputs.add(this.visit(output));
		}
		return new SMVFunction(function.id, smvInputs, smvOutputs);
	}

	@Override
	public SMVProgram visit(Program program) {
		List<SMVFunction> sMVFunctions = new ArrayList<SMVFunction>();
		List<SMVModule> sMVModules = new ArrayList<SMVModule>();
		
		if(!program.functions.isEmpty()) {
			for(Function function : program.functions) {
				sMVFunctions.add(this.visit(function));
			}
		}
		
		if (!program.nodes.isEmpty()) {
			for (Node node : program.nodes) {
				sMVModules.add(this.visit(node));
			}
		}

		return new SMVProgram(sMVFunctions, sMVModules, program.main);
	}

	@Override
	public SMVModule visit(Node node) {
		List<SMVVarDecl> sMVInputs = new ArrayList<SMVVarDecl>();
		List<SMVVarDecl> sMVOutputs = new ArrayList<SMVVarDecl>();
		List<SMVVarDecl> sMVlocals = new ArrayList<SMVVarDecl>();
		List<SMVEquation> sMVEquations = new ArrayList<SMVEquation>();
		List<SMVExpr> sMVAssertions = new ArrayList<SMVExpr>();
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
				sMVEquations.add(this.visit(eqn));
			}
		}
		if (!node.assertions.isEmpty()) {
			for (Expr asser : node.assertions) {
				sMVAssertions.add(this.visitExpr(asser));
			}
		}
		if (!node.properties.isEmpty()) {
			for (String property : node.properties) {
				sMVSpecifications.add(SMVValidId.replaceIllegalChar(property));
			}
		}
		return new SMVModule(node.id, sMVInputs, sMVOutputs, sMVlocals, sMVEquations, sMVAssertions, sMVSpecifications);
	}

	@Override
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

	@Override
	public SMVEquation visit(Equation eqn) {
		if (eqn.lhs.size() > 1) {
			throw new Lustre2SMVException("Lustre to SMV does not support more than one variable at LHS");
		}
		return new SMVEquation(this.visit(eqn.lhs.get(0)), this.visitExpr(eqn.expr));
	}

	public SMVExpr visitExpr(Expr expr) {
		return (SMVExpr) expr.accept(this);
	}

	@Override
	public SMVIdExpr visit(IdExpr expr) {
		return new SMVIdExpr(expr.id);
	}

	@Override
	public SMVIntExpr visit(IntExpr expr) {
		return new SMVIntExpr(expr.value);
	}

	@Override
	public SMVRealExpr visit(RealExpr expr) {
		return new SMVRealExpr(expr.value);
	}

	@Override
	public SMVArrayExpr visit(ArrayExpr expr) {
		return new SMVArrayExpr(this.visit(expr.elements));
	}

	public List<SMVExpr> visit(List<Expr> elements) {
		List<SMVExpr> sMVElements = new ArrayList<SMVExpr>();
		for (Expr element : elements) {
			sMVElements.add(this.visitExpr(element));
		}
		return sMVElements;
	}

	@Override
	public SMVUnaryExpr visit(UnaryExpr expr) {
		return new SMVUnaryExpr(this.visitUnaryOp(expr.op), this.visitExpr(expr.expr));
	}

	public SMVUnaryOp visitUnaryOp(UnaryOp uo) {
		return SMVUnaryOp.fromString(uo.toString());
	}

	@Override
	public SMVBoolExpr visit(BoolExpr expr) {
		return new SMVBoolExpr(expr.value);
	}

	@Override
	public SMVBinaryExpr visit(BinaryExpr expr) {
		return new SMVBinaryExpr(this.visitExpr(expr.left), visitBinaryOp(expr.op), this.visitExpr(expr.right));
	}

	public SMVBinaryOp visitBinaryOp(BinaryOp op) {
		return SMVBinaryOp.fromString(op.toString());
	}

	@Override
	public SMVCaseExpr visit(IfThenElseExpr expr) {
		return new SMVCaseExpr((SMVExpr) (expr.cond).accept(this), (SMVExpr) (expr.thenExpr).accept(this), this.visitExpr(expr.elseExpr));
	}

	@Override
	public SMVCastExpr visit(CastExpr expr) {
		return new SMVCastExpr(this.visit(expr.type), this.visitExpr(expr.expr));
	}

	@Override
	public SMVFunctionCallExpr visit(FunctionCallExpr expr) {
		return new SMVFunctionCallExpr(expr.function, this.visit(expr.args));
	}

	@Override
	public SMVAst visit(ArrayAccessExpr e) {
		throw new Lustre2SMVException("ArrayAccessExpr is not expected at this point.");
	}

	@Override
	public SMVAst visit(ArrayUpdateExpr e) {
		throw new Lustre2SMVException("ArrayUpdateExpr is not expected at this point.");
	}

	@Override
	public SMVAst visit(CondactExpr e) {
		throw new Lustre2SMVException("CondactExpr is not expected at this point.");
	}

	@Override
	public SMVAst visit(NodeCallExpr e) {
		throw new Lustre2SMVException("NodeCallExpr is not expected at this point.");
	}

	@Override
	public SMVAst visit(RecordAccessExpr e) {
		throw new Lustre2SMVException("RecordAccessExpr is not expected at this point.");
	}

	@Override
	public SMVAst visit(RecordExpr e) {
		throw new Lustre2SMVException("RecordExpr is not expected at this point.");
	}

	@Override
	public SMVAst visit(RecordUpdateExpr e) {
		throw new Lustre2SMVException("RecordUpdateExpr is not expected at this point.");
	}

	@Override
	public SMVAst visit(TupleExpr e) {
		throw new Lustre2SMVException("TupleExpr is not expected at this point.");
	}
}
