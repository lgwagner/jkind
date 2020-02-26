package jkind.smv;

import jkind.smv.visitors.SMV_Lus2SMV_Visitor;

public abstract class SMV_Lus2SMV_AST extends SMVAst {

	public abstract <T, S extends T> T accept(SMV_Lus2SMV_Visitor<T, S> visitor);

}
