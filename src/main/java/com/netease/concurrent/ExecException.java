package com.netease.concurrent;

public class ExecException extends Exception {
	private static final long serialVersionUID = -6447419812961499704L;

	public ExecException() {
	}

	public ExecException(String message) {
		super(message);
	}

	public ExecException(Throwable cause) {
		super(cause);
	}

	public ExecException(String message, Throwable cause) {
		super(message, cause);
	}

}
