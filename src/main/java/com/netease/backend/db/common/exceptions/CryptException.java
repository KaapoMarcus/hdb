package com.netease.backend.db.common.exceptions;

public class CryptException extends Exception{

	private static final long serialVersionUID = -6620849440548101329L;

	public CryptException()
	{
	}
	
	public CryptException(String desc)
	{
		super(desc);
	}
	
	public CryptException(Exception e)
	{
		super(e);
	}

}
