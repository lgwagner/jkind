package jkind.smv;

public enum SMVUnaryOp {

	NEGATIVE ("-"),
	NOT ("not"),
	PRE ("pre");
	
	private String str;
	
	private SMVUnaryOp(String str) {
		this.str = str;
	}
	
	@Override
	public String toString() {
		return str;
	}
	
	public static SMVUnaryOp fromString(String string) {
		for (SMVUnaryOp op : SMVUnaryOp.values()) {
			if (op.toString().equals(string)) {
				return op;
			}
		}
		return null;
	}
		
}
