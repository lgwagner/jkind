package jkind.smv;

import java.util.Collections;
import java.util.List;

import jkind.Assert;
import jkind.smv.visitors.SMVAstVisitor;
import jkind.util.Util;

public class SMVFunction extends SMVAst {
	public final String id;
	public final List<SMVVarDecl> inputs;
	public final List<SMVVarDecl> outputs;

	public SMVFunction(String id, List<SMVVarDecl> inputs, List<SMVVarDecl> outputs) {
		Assert.isNotNull(id);
		this.id = id;
		this.inputs = Util.safeList(inputs);
		this.outputs = Util.safeList(outputs);
	}

	public SMVFunction(String id, List<SMVVarDecl> inputs, SMVVarDecl output) {
		this(id, inputs, Collections.singletonList(output));
	}

	@Override
	public <T, S extends T> T accept(SMVAstVisitor<T, S> visitor) {
		return visitor.visit(this);
	}
}