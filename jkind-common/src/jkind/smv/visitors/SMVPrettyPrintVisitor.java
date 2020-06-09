package jkind.smv.visitors;

import java.util.Iterator;
import java.util.List;

import jkind.smv.SMVArrayAccessExpr;
import jkind.smv.SMVArrayExpr;
import jkind.smv.SMVArrayUpdateExpr;
import jkind.smv.SMVBinaryExpr;
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
import jkind.smv.SMVModuleCallExpr;
import jkind.smv.SMVNamedType;
import jkind.smv.SMVProgram;
import jkind.smv.SMVRealExpr;
import jkind.smv.SMVType;
import jkind.smv.SMVUnaryExpr;
import jkind.smv.SMVVarDecl;

public class SMVPrettyPrintVisitor implements SMVAstVisitor<Void, Void> {
	private SMVProgram program;
	private StringBuilder sb = new StringBuilder();
	@SuppressWarnings("unused")
	private String main;

	@Override
	public String toString() {
		return sb.toString();
	}

	protected void write(Object o) {
		sb.append(o);
	}

	private static final String seperator = System.getProperty("line.separator");

	private void newline() {
		write(seperator);
	}

	public Void visitFunction() {
		if (!program.functions.isEmpty()) {
			for (SMVFunction function : program.functions) {
				function.accept(this);
				newline();
			}
		}
		return null;
	}

	@Override
	public Void visit(SMVProgram program) {
		main = program.main;
		this.program = program;

		Iterator<SMVModule> iterator = program.modules.iterator();
		while (iterator.hasNext()) {
			iterator.next().accept(this);
			newline();
			if (iterator.hasNext()) {
				newline();
			}
		}
		return null;
	}

	@Override
	public Void visit(SMVModule module) {
		write("MODULE ");
		/*
		 * TODO there supposed to be only one module called "main". If it is otherwise
		 * then uncomment the following line.
		 */
		// write(module.id);
		write("main");
		newline();
		inputVarDecls(module.inputs);
		newline();
		varDecls(module.outputs);
		newline();
		varDecls(module.locals);
		newline();
		/*
		 * at this point, functions has to be appear unlike to Lustre in which functions
		 * before node's definition
		 */
		visitFunction();
		write("ASSIGN");
		newline();
		for (SMVEquation equation : module.equations) {
			write("  ");
			equation.accept(this);
			newline();
			newline();
		}

		for (SMVExpr assertion : module.assertions) {
			write("  INVAR ");
			expr(assertion);
			write(";");
			newline();
		}

		if (!module.sMVSpecifications.isEmpty()) {
			for (String property : module.sMVSpecifications) {
				property(property);
			}
			newline();
		}

		return null;
	}

	private void inputVarDecls(List<SMVVarDecl> inputs) {
		Iterator<SMVVarDecl> iterator = inputs.iterator();
		while (iterator.hasNext()) {
			write("  VAR ");
			iterator.next().accept(this);
			write(";");
			if (iterator.hasNext()) {
				newline();
			}
		}
	}

	private void varDecls(List<SMVVarDecl> inputs) {
		Iterator<SMVVarDecl> iterator = inputs.iterator();
		while (iterator.hasNext()) {
			write("  VAR ");
			iterator.next().accept(this);
			write(";");
			if (iterator.hasNext()) {
				newline();
			}
		}
	}

	@Override
	public Void visit(SMVVarDecl smvVarDecl) {
		write(smvVarDecl.id);
		write(" : ");
		write(smvVarDecl.type);
		return null;
	}

	@Override
	public Void visit(SMVEquation smvEquation) {
		if (smvEquation.lhs == null) {
			write("()");
		} else {
			write(smvEquation.lhs);
		}
		write(" := ");
		expr(smvEquation.expr);
		write(";");
		return null;
	}

	public void expr(SMVExpr e) {
		e.accept(this);
	}

	protected void property(String s) {
		write("LTLSPEC G(");
		write(s);
		write(")");
		newline();
	}

	@Override
	public Void visit(SMVBinaryExpr e) {
		write("(");
		expr(e.left);
		write(" ");
		if (e.op.toString().equals("=>")) {
			write("->");
		} else {
			write(e.op);
		}
		write(" ");
		expr(e.right);
		write(")");
		return null;
	}

	@Override
	public Void visit(SMVBoolExpr e) {
		String str = Boolean.toString(e.value);
		if (str.contentEquals("true")) {
			str = "TRUE";
		}
		if (str.contentEquals("false")) {
			str = "FALSE";
		}
		write(str);
		return null;
	}

	@Override
	public Void visit(SMVIdExpr e) {
		write(e.id);
		return null;
	}

	@Override
	public Void visit(SMVIntExpr e) {
		write(e.value);
		return null;
	}

	@Override
	public Void visit(SMVRealExpr e) {
		write(e.value);
		return null;
	}

	@Override
	public Void visit(SMVUnaryExpr e) {
		write("(");
		write(e.op);
		expr(e.expr);
		write(")");
		return null;
	}

	@Override
	public Void visit(SMVCaseExpr e) {
		newline();
		write("		");
		write("(case ");
		newline();
		write("			");
		expr(e.cond);
		write(" : ");
		expr(e.thenExpr);
		write(";");
		newline();
		write("			");
		write(" TRUE : ");
		expr(e.elseExpr);
		write(";");
		newline();
		write("		");
		write("esac)");
		return null;
	}

	@Override
	public Void visit(SMVCastExpr e) {
		write(getCastFunction(e.type));
		write("(");
		expr(e.expr);
		write(")");
		return null;
	}

	private String getCastFunction(SMVType type) {
		if (type == SMVNamedType.INT) {
			return "floor";
		} else {
			throw new IllegalArgumentException("Unable to cast to type: " + type);
		}
	}

	@Override
	public Void visit(SMVFunction smvFunction) {
		write("  FUN ");
		write(smvFunction.id);
		write(" : ");
		Iterator<SMVVarDecl> iterator = smvFunction.inputs.iterator();
		while (iterator.hasNext()) {
			write(iterator.next().type);
			if (iterator.hasNext()) {
				write(" * ");
			}
		}
		write(" -> ");
		Iterator<SMVVarDecl> iterator1 = smvFunction.outputs.iterator();
		while (iterator1.hasNext()) {
			write(iterator1.next().type);
			if (iterator1.hasNext()) {
				write(" * ");
			}
		}
		write(";");
		return null;
	}

	@Override
	public Void visit(SMVFunctionCallExpr e) {
		write(e.function);
		write("(");
		Iterator<SMVExpr> iterator = e.args.iterator();
		while (iterator.hasNext()) {
			expr(iterator.next());
			if (iterator.hasNext()) {
				write(", ");
			}
		}
		write(")");
		return null;
	}

	@Override
	public Void visit(SMVArrayAccessExpr e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(SMVArrayExpr e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(SMVArrayUpdateExpr e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void visit(SMVModuleCallExpr e) {
		// TODO Auto-generated method stub
		return null;
	}

}
