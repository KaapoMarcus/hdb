package com.netease.backend.db.common.exceptions;


public class OnlineAlterTableException extends Exception {
	private static final long serialVersionUID = 1L;

	
	public OnlineAlterTableException(String msg) {
		super(msg);
	}

	
	public OnlineAlterTableException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
