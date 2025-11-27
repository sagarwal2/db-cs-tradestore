package com.dws.casestudy.tradestore.exception;

public class MaturityInPastException extends RuntimeException {
	private static final long serialVersionUID = -3261611395052443930L;

	public MaturityInPastException(String message) { super(message); }
}
