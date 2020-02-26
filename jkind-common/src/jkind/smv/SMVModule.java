package jkind.smv;

import java.util.List;

import jkind.Assert;
import jkind.smv.visitors.SMVAstVisitor;

public class SMVModule extends SMVAst {

	public final String id;
	public final List<SMVVarDecl> inputs;
	public final List<SMVVarDecl> outputs;
	public final List<SMVVarDecl> locals;
	public final List<SMVEquation> equations;

	public SMVModule(String id, List<SMVVarDecl> inputs, List<SMVVarDecl> outputs, List<SMVVarDecl> locals, List<SMVEquation> sMVEquations) {

		Assert.isNotNull(id);
		this.id = id;
		this.inputs = inputs;
		this.outputs = outputs;
		this.locals = locals;
		this.equations = sMVEquations;
	}

	@Override
	public <T, S extends T> T accept(SMVAstVisitor<T, S> visitor) {
		return visitor.visit(this);
	}
}
