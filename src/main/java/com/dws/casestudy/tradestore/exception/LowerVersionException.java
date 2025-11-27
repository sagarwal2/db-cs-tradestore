package com.dws.casestudy.tradestore.exception;

public class LowerVersionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public LowerVersionException(String message) { super(message); }
}
