package com.netease.backend.db.common.exceptions;

public class BackupException extends Exception 
{

	private static final long serialVersionUID = -7803853227545087623L;

	public BackupException()
	{
	}
	
	public BackupException(String desc)
	{
		super(desc);
	}
	
	public BackupException(Exception e)
	{
		super(e);
	}
}
