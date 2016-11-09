
package com.netease.backend.db.common.exceptions;


public class MSRuntimeException extends RuntimeException {

	private static final long	serialVersionUID	= 1889600442422107661L;

	
	public MSRuntimeException() {
	}

	
	public MSRuntimeException(
		String message)
	{
		super(message);
	}

	
	public MSRuntimeException(
		Throwable cause)
	{
		super(cause);
	}

	
	public MSRuntimeException(
		String message,
		Throwable cause)
	{
		super(message, cause);
	}

}
