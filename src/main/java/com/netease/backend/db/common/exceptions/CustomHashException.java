package com.netease.backend.db.common.exceptions;


public class CustomHashException extends Exception {

	private static final long serialVersionUID = 1L;

	public CustomHashException() {
		
	}
	
	public CustomHashException(String msg) {
		super(msg);
	}
	
	public CustomHashException(Exception e) {
		super(e);
	}
}
