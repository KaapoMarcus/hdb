package com.netease.backend.db.common.exceptions;


public class OnlineMigrateException extends Exception {
	private static final long serialVersionUID = 1L;

	
	public OnlineMigrateException(String msg) {
		super(msg);
	}

	
	public OnlineMigrateException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
