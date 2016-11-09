
package com.netease.backend.db.common.cmd;


public class DDBCmdException extends CmdException {
	private static final long	serialVersionUID	= -6322855844877613789L;

	public DDBError getError() {
		return DDBError.valueOf(this.getCode());
	}

	@Override
	public String getMessage() {
		return this.getErrMessage();
	}

	
	public String getErrMessage() {
		final String base = DDBError.valueOf(this.getCode()).toString();
		final String msg;
		return ((msg = super.getMessage()) != null) ? (base + ", " + msg)
				: base;
	}

	
	public DDBCmdException(
		int code,
		String message,
		Throwable cause)
	{
		super(code, message, cause);
	}

	
	public DDBCmdException(
		int code)
	{
		super(code);
	}

	
	public DDBCmdException(
		int code,
		String message)
	{
		super(code, message);
	}

	
	public DDBCmdException(
		int code,
		Throwable cause)
	{
		super(code, cause);
	}

	
	public DDBCmdException(
		DDBError err,
		String message,
		Throwable cause)
	{
		super(err.code, message, cause);
	}

	
	public DDBCmdException(
		DDBError err)
	{
		super(err.code);
	}

	
	public DDBCmdException(
		DDBError err,
		String message)
	{
		super(err.code, message);
	}

	
	public DDBCmdException(
		DDBError err,
		Throwable cause)
	{
		super(err.code, cause);
	}

}
