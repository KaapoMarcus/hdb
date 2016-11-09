
package com.netease.backend.db.common.cmd;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;


public class CmdException extends IOException {
	private static final long	serialVersionUID	= 9135442671014561884L;
	private final int			code;

	
	private final Exception		proxy;

	public int getCode() {
		return code;
	}

	public CmdException(
		int code,
		String message,
		Throwable cause)
	{
		proxy = new Exception(message, cause);
		this.code = code;
	}

	public CmdException(
		int code)
	{
		proxy = new Exception();
		this.code = code;
	}

	public CmdException(
		int code,
		String message)
	{
		proxy = new Exception(message);
		this.code = code;
	}

	public CmdException(
		int code,
		Throwable cause)
	{
		proxy = new Exception(cause);
		this.code = code;
	}

	
	@Override
	public Throwable fillInStackTrace() {
		return (proxy != null) ? proxy.fillInStackTrace() : null;
	}

	
	@Override
	public Throwable getCause() {
		return proxy.getCause();
	}

	
	@Override
	public String getLocalizedMessage() {
		return proxy.getLocalizedMessage();
	}

	
	@Override
	public String getMessage() {
		return proxy.getMessage();
	}

	
	@Override
	public StackTraceElement[] getStackTrace() {
		return proxy.getStackTrace();
	}

	
	@Override
	public Throwable initCause(
		Throwable cause)
	{
		return proxy.initCause(cause);
	}

	
	@Override
	public void printStackTrace() {
		proxy.printStackTrace();
	}

	
	@Override
	public void printStackTrace(
		PrintStream s)
	{
		proxy.printStackTrace(s);
	}

	
	@Override
	public void printStackTrace(
		PrintWriter s)
	{
		proxy.printStackTrace(s);
	}

	
	@Override
	public void setStackTrace(
		StackTraceElement[] stackTrace)
	{
		proxy.setStackTrace(stackTrace);
	}

}
