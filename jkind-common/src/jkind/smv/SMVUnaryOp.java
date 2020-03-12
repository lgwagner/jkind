package jkind.smv;

public enum SMVUnaryOp {

	SMVNEGATIVE("-"), 
	SMVNOT("!"), 
	SMVPRE(" ");

	private String str;

	private SMVUnaryOp(String str) {
		this.str = str;
	}

	@Override
	public String toString() {
		return str;
	}

	public static SMVUnaryOp fromString(String string) {
		if (string.equals("-")) {
			return SMVNEGATIVE;
		}
		if (string.equals("not")) {
			return SMVNOT;
		}
		if (string.equals("pre")) {
			return SMVPRE;
		}
		return null;
	}

}
