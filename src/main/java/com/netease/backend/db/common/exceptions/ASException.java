package com.netease.backend.db.common.exceptions;


public class ASException extends Exception {
	private static final long	serialVersionUID	= 7172529063399444877L;

	public ASException() {
		super();
	}

	public ASException(
		String message,
		Throwable cause)
	{
		super(message, cause);
	}

	public ASException(
		String message)
	{
		super(message);
	}

	public ASException(
		Throwable cause)
	{
		super(cause);
	}

}
