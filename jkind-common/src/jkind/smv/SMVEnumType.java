package jkind.smv;

import java.util.List;

import jkind.Assert;
import jkind.lustre.EnumType;
import jkind.smv.visitors.SMVTypeVisitor;
import jkind.util.Util;

public class SMVEnumType extends SMVType {
	public final String id;
	public final List<String> values;

	public SMVEnumType(String id, List<String> values) {
		Assert.isNotNull(id);
		this.id = id;
		this.values = Util.safeList(values);
	}

	public String getValue(int i) {
		return values.get(i);
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EnumType) {
			EnumType et = (EnumType) obj;
			return id.equals(et.id);
		}
		return false;
	}

	@Override
	public <T> T accept(SMVTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
