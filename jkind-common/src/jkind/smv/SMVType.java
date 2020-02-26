package jkind.smv;

import jkind.smv.visitors.SMVTypeVisitor;

public abstract class SMVType {

	public abstract <T> T accept(SMVTypeVisitor<T> visitor);
}
