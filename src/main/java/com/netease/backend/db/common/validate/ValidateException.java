
package com.netease.backend.db.common.validate;


public class ValidateException extends Exception {

	private static final long	serialVersionUID	= 9023177323708825824L;

	public ValidateException() {
	}

	
	public ValidateException(String message) {
		super(message);
	}

	
	public ValidateException(Throwable cause) {
		super(cause);
	}

	
	public ValidateException(String message, Throwable cause) {
		super(message, cause);
	}

}
