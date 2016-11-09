package com.jhh.hdb.proxyserver.codec;

/**
 * 数据包编码异常
 * 
 *
 */
public class EncodeException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EncodeException() {
		super();
	}

	public EncodeException(String message) {
		super(message);
	}

	public EncodeException(String message, Throwable cause) {
		super(message, cause);
	}

	public EncodeException(Throwable cause) {
		super(cause);
	}
}
