package jkind.smv.visitors;

import java.util.Iterator;
import java.util.List;

import jkind.lustre.Node;
import jkind.lustre.Program;
import jkind.smv.SMVBinaryExpr;
import jkind.smv.SMVBoolExpr;
import jkind.smv.SMVEquation;
import jkind.smv.SMVExpr;
import jkind.smv.SMVIdExpr;
import jkind.smv.SMVIntExpr;
import jkind.smv.SMVModule;
import jkind.smv.SMVProgram;
import jkind.smv.SMVType;
import jkind.smv.SMVUnaryExpr;
import jkind.smv.SMVVarDecl;

public class SMVPrettyPrintVisitor implements SMVAstVisitor {
	private StringBuilder sb = new StringBuilder();
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

	@Override
	public Void visit(SMVProgram program) {
		main = program.main;

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
		write(module.id);
		newline();
		varDecls(module.inputs);
		newline();
		varDecls(module.outputs);
		newline();
		varDecls(module.locals);
		newline();
		write("ASSIGN");
		newline();
		for (SMVEquation equation : module.equations) {
			write("  ");
			equation.accept(this);
			newline();
			newline();
		}
		
		return null;
	}

	@Override
	public Object visit(Program program) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visit(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	private void varDecls(List<SMVVarDecl> inputs) {
		Iterator<SMVVarDecl> iterator = inputs.iterator();
		while (iterator.hasNext()) {
			write("  VAR ");
			iterator.next().accept(this);
			if (iterator.hasNext()) {
				write(";");
				newline();
			}
		}
	}

	@Override
	public Object visit(SMVVarDecl smvVarDecl) {
		// TODO Auto-generated method stub
		write(smvVarDecl.id);
		write(" : ");
		write(smvVarDecl.type);
		return null;
	}

	@Override
	public Object visit(SMVEquation smvEquation) {
		if (smvEquation.lhs.isEmpty()) {
			write("()");
		} else {
			Iterator<SMVIdExpr> iterator = smvEquation.lhs.iterator();
			while (iterator.hasNext()) {
				write(iterator.next().id);
				if (iterator.hasNext()) {
					write(", ");
				}
			}
		}

		write(" = ");
		expr(smvEquation.expr);
		write(";");
		return null;
	}
	
	public void expr(SMVExpr e) {
		e.accept(this);
	}
	
	@Override
	public Void visit(SMVBinaryExpr e) {
		write("(");
		expr(e.left);
		write(" ");
		write(e.op);
		write(" ");
		expr(e.right);
		write(")");
		return null;
	}

	@Override
	public Void visit(SMVBoolExpr e) {
		write(Boolean.toString(e.value));
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
	public Object visit(SMVUnaryExpr e) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
