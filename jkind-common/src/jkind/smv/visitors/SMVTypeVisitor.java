package jkind.smv.visitors;

import jkind.smv.SMVArrayType;
import jkind.smv.SMVEnumType;
import jkind.smv.SMVNamedType;
import jkind.smv.SMVSubrangeIntType;

public interface SMVTypeVisitor<T> {
	public T visit(SMVArrayType e);

	public T visit(SMVEnumType e);

	public T visit(SMVNamedType e);

	public T visit(SMVSubrangeIntType e);
}
