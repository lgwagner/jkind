package jkind.smv.visitors;

import jkind.smv.SMVArrayType;
import jkind.smv.SMVNamedType;
import jkind.smv.SMVSubrangeIntType;

public interface SMVTypeVisitor<T> {
	public T visit(SMVArrayType e);
	//public T visit(EnumType e);
	public T visit(SMVNamedType e);
	//public T visit(RecordType e);
	//public T visit(TupleType e);
	public T visit(SMVSubrangeIntType e);
}
