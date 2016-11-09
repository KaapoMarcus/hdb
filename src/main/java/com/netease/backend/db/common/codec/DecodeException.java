package com.netease.backend.db.common.codec;

public class DecodeException extends Exception {

	
	private static final long serialVersionUID = 1L;

	public DecodeException(String msg) {
		super(msg);
	}
	
	public DecodeException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
	@SuppressWarnings("unused")
	private DecodeException() {
		
	}
}
