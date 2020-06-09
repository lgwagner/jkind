package jkind.smv.util;

import jkind.JKindException;

public class Lustre2SMVException extends JKindException {
	private static final long serialVersionUID = 2L;

	public Lustre2SMVException(String message) {
		super(message);
	}
	
	public Lustre2SMVException(String message, Throwable t) {
		super(message, t);
	}
}
