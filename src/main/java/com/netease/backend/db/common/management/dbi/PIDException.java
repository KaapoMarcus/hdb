
package com.netease.backend.db.common.management.dbi;


public class PIDException extends Exception {
	private static final long	serialVersionUID	= -6187011078686288378L;

	
	public PIDException() {
	}

	
	public PIDException(
		String message)
	{
		super(message);
	}

	
	public PIDException(
		Throwable cause)
	{
		super(cause);
	}

	
	public PIDException(
		String message,
		Throwable cause)
	{
		super(message, cause);
	}

}
