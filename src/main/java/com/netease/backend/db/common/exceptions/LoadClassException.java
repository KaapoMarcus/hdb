package com.netease.backend.db.common.exceptions;


public class LoadClassException extends Exception {

	private static final long serialVersionUID = 1L;

	public LoadClassException() {
		
	}
	
	public LoadClassException(String msg) {
		super(msg);
	}
	
	public LoadClassException(Exception e) {
		super(e);
	}
}
