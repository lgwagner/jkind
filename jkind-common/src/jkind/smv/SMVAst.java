package jkind.smv;

import jkind.smv.visitors.SMVAstVisitor;
import jkind.smv.visitors.SMVPrettyPrintVisitor;

public abstract class SMVAst {

	// TODO: not sure we're going to want this, yet.
	// public final Location location;

	@Override
	public String toString() {
		SMVPrettyPrintVisitor visitor = new SMVPrettyPrintVisitor();
		accept(visitor);
		return visitor.toString();
	}

	// TODO: we may have to write a visitor to pretty print an SMV Ast
	// public abstract <T, S extends T> T accept(AstVisitor<T, S> visitor);
	public abstract <T, S extends T> T accept(SMVAstVisitor<T, S> visitor);
}
