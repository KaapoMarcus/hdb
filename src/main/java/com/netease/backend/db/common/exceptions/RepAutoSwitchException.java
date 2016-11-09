package com.netease.backend.db.common.exceptions;


public class RepAutoSwitchException extends Exception {

	
	private static final long serialVersionUID = 1L;

	
	public RepAutoSwitchException(String msg) {
		super(msg);
	}

	
	public RepAutoSwitchException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
