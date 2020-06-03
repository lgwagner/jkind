package jkind.smv;

import jkind.Assert;
import jkind.smv.util.SMVUtil;
import jkind.smv.visitors.SMVAstVisitor;

public class SMVVarDecl extends SMVAst {
	public final String id;
	public SMVType type;

	public SMVVarDecl(String id, SMVType type) {
		Assert.isNotNull(id);
		Assert.isNotNull(type);

		this.id = SMVUtil.replaceIllegalChar(id);
		this.type = type;
	}

	public SMVVarDecl(String id, SMVSubrangeIntType type) {
		Assert.isNotNull(id);
		Assert.isNotNull(type);

		this.id = SMVUtil.replaceIllegalChar(id);
		this.type = type;
	}

	@Override
	public <T, S extends T> T accept(SMVAstVisitor<T, S> visitor) {
		return visitor.visit(this);
	}

}
