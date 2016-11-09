package com.netease.backend.db.common.exceptions;


public class LoadBalanceException extends Exception {
	private static final long serialVersionUID = 1L;

	
	public LoadBalanceException(String msg) {
		super(msg);
	}

	
	public LoadBalanceException(String msg, Throwable cause) {
		super(msg, cause);
	}
}