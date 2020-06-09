package jkind.smv;

import jkind.Assert;
import jkind.smv.visitors.SMVTypeVisitor;

public class SMVArrayType extends SMVType {
	public final SMVType base;
	public final int size;

	public SMVArrayType(SMVType base, int size) {
		Assert.isNotNull(base);
		Assert.isTrue(size > 0);
		this.base = base;
		this.size = size;
	}

	@Override
	public String toString() {
		return base + "[" + size + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		result = prime * result + size;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SMVArrayType)) {
			return false;
		}
		SMVArrayType other = (SMVArrayType) obj;
		if (base == null) {
			if (other.base != null) {
				return false;
			}
		} else if (!base.equals(other.base)) {
			return false;
		}
		if (size != other.size) {
			return false;
		}
		return true;
	}

	@Override
	public <T> T accept(SMVTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
