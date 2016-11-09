
package com.netease.backend.db.common.utils;

public class Log4jConfigError extends RuntimeException {
	private static final long	serialVersionUID	= -8655128882294733356L;

	public Log4jConfigError() {
		super();
	}

	public Log4jConfigError(
		String message,
		Throwable cause)
	{
		super(message, cause);
	}

	public Log4jConfigError(
		String message)
	{
		super(message);
	}

	public Log4jConfigError(
		Throwable cause)
	{
		super(cause);
	}

}
