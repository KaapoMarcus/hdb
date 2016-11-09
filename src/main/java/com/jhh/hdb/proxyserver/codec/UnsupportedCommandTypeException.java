package com.jhh.hdb.proxyserver.codec;

/**
 * 命令不支持异常，只适用于QSRequestDecoder中，便于QSCmdHandler进行特殊处理
 * 
 *
 */
public class UnsupportedCommandTypeException extends Exception {

	private static final long serialVersionUID = 1L;

	public UnsupportedCommandTypeException() {
		super();
	}

	public UnsupportedCommandTypeException(String message) {
		super(message);
	}

	public UnsupportedCommandTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsupportedCommandTypeException(Throwable cause) {
		super(cause);
	}
}
