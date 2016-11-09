package com.netease.backend.db.common.exceptions;



public class IllegalResultException extends DBAException {

	private static final long serialVersionUID = 552747184408713089L;

	public IllegalResultException() {
		super();
	}

	public IllegalResultException(String message) {
		super(message);
	}

	public IllegalResultException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalResultException(Throwable cause) {
		super(cause);
	}
}
