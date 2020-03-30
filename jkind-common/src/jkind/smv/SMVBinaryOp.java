package jkind.smv;

public enum SMVBinaryOp {

	PLUS("+"), 
	MINUS("-"), 
	MULTIPLY("*"), 
	DIVIDE("/"), 
	INT_DIVIDE("div"), 
	MODULUS("mod"), 
	EQUAL("="), 
	NOTEQUAL("<>"),
	GREATER(">"), 
	LESS("<"), 
	GREATEREQUAL(">="), 
	LESSEQUAL("<="), 
	OR("or"), 
	AND("and"), 
	XOR("xor"), 
	IMPLIES("=>"),
	ARROW("->"),

	SMVNOTEQUAL("!="), SMVAND("&"), SMVOR("|");
	// SMVIMPLY("->");

	private String str;

	private SMVBinaryOp(String str) {
		this.str = str;
	}

	@Override
	public String toString() {
		return str;
	}

	public static SMVBinaryOp fromString(String string) {
		for (SMVBinaryOp op : SMVBinaryOp.values()) {
			if (string.equals("and")) {
				return SMVAND;
			}
			if (string.equals("or")) {
				return SMVOR;
			}
			if (string.equals("<>")) {
				return SMVNOTEQUAL;
			}
			if (op.toString().equals(string)) {
				return op;
			}
		}
		throw new IllegalArgumentException("Unknown binary operator: " + string);
	}

}
