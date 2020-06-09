package jkind.smv.visitors;

import jkind.smv.SMVArrayType;
import jkind.smv.SMVEnumType;
import jkind.smv.SMVNamedType;
import jkind.smv.SMVSubrangeIntType;
import jkind.smv.SMVType;

public class SMVTypeMapVisitor implements SMVTypeVisitor<SMVType> {
	@Override
	public SMVType visit(SMVArrayType e) {
		return new SMVArrayType(e.base.accept(this), e.size);
	}

	@Override
	public SMVType visit(SMVNamedType e) {
		return e;
	}

	@Override
	public SMVType visit(SMVEnumType e) {
		return e;
	}

	@Override
	public SMVType visit(SMVSubrangeIntType e) {
		return e;
	}
}
