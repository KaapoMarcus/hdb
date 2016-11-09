package com.jhh.hdb.proxyserver.codec;

/**
 * 解码异常
 * 
 *
 */
public class DecodeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DecodeException() {
		super();
	}

	public DecodeException(String message) {
		super(message);
	}

	public DecodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public DecodeException(Throwable cause) {
		super(cause);
	}
}
