package com.netease.backend.db.common.exceptions;



public class AlarmException extends RuntimeException 
{
	private static final long serialVersionUID = 4124555741761650805L;

	public AlarmException()
	{
	}
	
	public AlarmException(String desc)
	{
		super(desc);
	}
}
