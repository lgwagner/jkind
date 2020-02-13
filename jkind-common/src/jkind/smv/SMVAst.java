package jkind.smv;

import jkind.Assert;
import jkind.lustre.Location;

public abstract class SMVAst {
		
	//TODO: not sure we're going to want this, yet.
	public final Location location;

	public SMVAst(Location location) {
		Assert.isNotNull(location);
		this.location = location;
	}

	@Override
	public String toString() {
//		PrettyPrintVisitor visitor = new PrettyPrintVisitor();
//		accept(visitor);
//		return visitor.toString();
		return null;
	}

	//TODO: we may have to write a visitor to pretty print an SMV Ast
	//public abstract <T, S extends T> T accept(AstVisitor<T, S> visitor);
}
