package com.netease.backend.db.common.exceptions;



public class DBAException extends Exception {

	private static final long serialVersionUID = 4381508119344431460L;

	public DBAException() {
		super();
	}

	public DBAException(String message) {
		super(message);
	}

	public DBAException(String message, Throwable cause) {
		super(message, cause);
	}

	public DBAException(Throwable cause) {
		super(cause);
	}
}
