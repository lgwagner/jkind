package jkind.smv;

import java.math.BigInteger;

import jkind.Assert;
import jkind.smv.visitors.SMVTypeVisitor;

public class SMVSubrangeIntType extends SMVType{
	public final BigInteger low;
	public final BigInteger high;

	public SMVSubrangeIntType(BigInteger low, BigInteger high) {
		Assert.isNotNull(low);
		Assert.isNotNull(high);
		Assert.isTrue(low.compareTo(high) <= 0);
		this.low = low;
		this.high = high;
	}

	@Override
	public String toString() {
		return low + ".." + high;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((high == null) ? 0 : high.hashCode());
		result = prime * result + ((low == null) ? 0 : low.hashCode());
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
		if (!(obj instanceof SMVSubrangeIntType)) {
			return false;
		}
		SMVSubrangeIntType other = (SMVSubrangeIntType) obj;
		if (high == null) {
			if (other.high != null) {
				return false;
			}
		} else if (!high.equals(other.high)) {
			return false;
		}
		if (low == null) {
			if (other.low != null) {
				return false;
			}
		} else if (!low.equals(other.low)) {
			return false;
		}
		return true;
	}

	@Override
	public <T> T accept(SMVTypeVisitor<T> visitor) {
		return visitor.visit(this);
	}
}
