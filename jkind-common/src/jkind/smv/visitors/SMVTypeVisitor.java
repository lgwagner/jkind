package jkind.smv.visitors;

import jkind.smv.SMVNamedType;

public interface SMVTypeVisitor<T> {
	//public T visit(ArrayType e);
	//public T visit(EnumType e);
	public T visit(SMVNamedType e);
	//public T visit(RecordType e);
	//public T visit(TupleType e);
	//public T visit(SMVSubrangeIntType e);
}
