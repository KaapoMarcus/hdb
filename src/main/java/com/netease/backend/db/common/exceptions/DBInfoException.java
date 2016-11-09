package com.netease.backend.db.common.exceptions;


public class DBInfoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	
	public DBInfoException(String msg) {
		super(msg);
	}

	
	public DBInfoException(String msg, Throwable cause) {
		super(msg, cause);
	}
}