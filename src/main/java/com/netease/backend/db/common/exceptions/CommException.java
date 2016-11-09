package com.netease.backend.db.common.exceptions;



public class CommException extends Exception 
{
	private static final long serialVersionUID = 1668285726778132834L;
	
	
	public static int CONNECTION_ERROR = 1000;
	
	private int errno = 0;
	
	public CommException(String desc)
	{
		super(desc);
	}
	
	public CommException(Exception e)
	{
		super(e);
	}
	
	public CommException(String desc, int errno)
	{
		super(desc);
		this.errno = errno;
	}
	
	public CommException(Exception e, int errno)
	{
		super(e);
		this.errno = errno;
	}
	
	public int getErrno()
	{
		return this.errno;
	}
	
	public void setErrno(int errerNo)
	{
		this.errno = errerNo;
	}
	
}
