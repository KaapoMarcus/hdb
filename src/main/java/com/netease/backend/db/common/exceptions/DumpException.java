package com.netease.backend.db.common.exceptions;



public class DumpException extends Exception 
{
	private static final long serialVersionUID = -8970112969541314303L;

	public DumpException()
	{
	}
	
	public DumpException(String desc)
	{
		super(desc);
	}
	
	public DumpException(Exception e)
	{
		super(e);
	}
}
