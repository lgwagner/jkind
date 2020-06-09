package jkind.smv;

import jkind.Assert;
import jkind.smv.visitors.SMVTypeVisitor;

public class SMVNamedType extends SMVType {
	public final String name;

	public SMVNamedType(String name) {
		Assert.isNotNull(name);
		Assert.isFalse(name.equals(BOOL.toString()));
		Assert.isFalse(name.equals(INT.toString()));
		Assert.isFalse(name.equals(REAL.toString()));
		this.name = name;
	}

	/*
	 * Private constructor for built-in types
	 */
	private SMVNamedType(String name, @SuppressWarnings("unused") Object unused) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static final SMVNamedType BOOL = new SMVNamedType("boolean", null);
	public static final SMVNamedType INT = new SMVNamedType("integer", null);
	public static final SMVNamedType REAL = new SMVNamedType("real", null);

	public boolean isBuiltin() {
		return this == REAL || this == BOOL || this == INT;
	}

	public static SMVNamedType get(String name) {
		switch (name) {
		case "int":
			return SMVNamedType.INT;
		case "real":
			return SMVNamedType.REAL;
		case "bool":
			return SMVNamedType.BOOL;
		default:
			return new SMVNamedType(name);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof SMVNamedType)) {
			return false;
		}
		SMVNamedType other = (SMVNamedType) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public <T> T accept(SMVTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
