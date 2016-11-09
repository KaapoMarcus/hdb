package com.netease.backend.db.common.exceptions;


public class DataValidateException extends Exception {

	
	private static final long serialVersionUID = 1L;

	public DataValidateException() {
		
	}
	
	public DataValidateException(String msg) {
		super(msg);
	}
	
	public DataValidateException(Exception e) {
		super(e);
	}
}
