package com.netease.backend.db.common.exceptions;

public class MigException extends Exception {

	private static final long serialVersionUID = 1347473711469638419L;
	
	
	public static final int DUMP_SOURCE_BUCKET_ERROR = 98;
	
	
	public static final int COPY_TMP_TABLE_ERROR = 99;
	
	
	public static final int DELETE_SOURCE_BUCKET_ERROR = 100;
	
	
	public static final int DELETE_DEST_BUCKET_ERROR = 200;
	
	
	public static final int LOAD_DEST_BUCKET_ERROR = 201;
	
	
	public static final int CONNECTION_ERROR = 1001;
	
	
	public static final int ERR_LOCK_TIME_OUT = 1205;
	
	
	public static final int MENUAL_INTERRUPTED = 300;
	
	private int errno = 0;
	
	public MigException(String desc)
	{
		super(desc);
	}
	
	public MigException(Exception e)
	{
		super(e);
	}
	
	public MigException(String desc, int errno)
	{
		super(desc);
		this.errno = errno;
	}
	
	public MigException(Exception e, int errno)
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
