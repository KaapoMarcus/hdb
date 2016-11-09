package com.netease.backend.db.common.exceptions;



public class MSException extends Exception {

	private static final long	serialVersionUID	= 7172529063399444877L;

	public static final int		ERROR_CONN_TIME_OUT	= 1;

	private int					errno				= 0;

	public MSException() {
	}

	public MSException(
		String desc)
	{
		super(desc);
	}

	public MSException(
		Exception e)
	{
		super(e);
	}

	public MSException(
		Exception e,
		int errno)
	{
		super(e);
		this.errno = errno;
	}

	public MSException(
		String desc,
		int errno)
	{
		super(desc);
		this.errno = errno;
	}

	public int getErrno() {
		return errno;
	}

	
	public MSException(
		String message,
		Throwable cause)
	{
		super(message, cause);
	}

}
