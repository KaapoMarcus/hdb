package com.netease.backend.db.common.exceptions;


public class SysDBException extends Exception {
	private static final long	serialVersionUID	= 2247059249419143892L;

	
	public SysDBException() {
		
	}

	
	public SysDBException(
		String message,
		Throwable cause)
	{
		super(message, cause);
	}

	
	public SysDBException(
		String message)
	{
		super(message);
	}

	
	public SysDBException(
		Throwable cause)
	{
		super(cause);
	}

}
