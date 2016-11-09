package com.netease.backend.db.common.codec;

public class EncodeException extends Exception {

	
	private static final long serialVersionUID = 1L;
	
	public EncodeException(String msg) {
		super(msg);
	}
	
	public EncodeException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	@SuppressWarnings("unused")
	private EncodeException() {
		
	}

}
