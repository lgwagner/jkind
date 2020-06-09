package jkind.smv.visitors;

import jkind.smv.SMVExpr;
import jkind.smv.SMVModule;
import jkind.smv.SMVProgram;
import jkind.smv.SMVType;

public class SMVTypeAwareAstMapVisitor extends SMVAstMapVisitor {
	protected SMVTypeReconstructor typeReconstructor;

	protected SMVType getType(SMVExpr e) {
		return e.accept(typeReconstructor);
	}

	@Override
	public SMVProgram visit(SMVProgram e) {
		typeReconstructor = new SMVTypeReconstructor(e);
		return super.visit(e);
	}

	@Override
	public SMVModule visit(SMVModule e) {
		typeReconstructor.setModuleContext(e);
		return super.visit(e);
	}

}
