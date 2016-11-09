package com.netease.hotswap;

public class HotSwapException extends Exception {
	private static final long serialVersionUID = 5751252924914028023L;

	
	public HotSwapException(String message) {
		super(message);
	}

	
	public HotSwapException(String message, Throwable cause) {
		super(message, cause);
	}

	public HotSwapException(Throwable cause) {
		super(cause);
	}
}
