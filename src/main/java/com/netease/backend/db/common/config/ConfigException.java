
package com.netease.backend.db.common.config;

public class ConfigException extends Exception {
	private static final long	serialVersionUID	= 1881040701444709413L;

	
	public ConfigException() {
	}

	
	public ConfigException(
		String message)
	{
		super(message);
	}

	
	public ConfigException(
		Throwable cause)
	{
		super(cause);
	}

	
	public ConfigException(
		String message,
		Throwable cause)
	{
		super(message, cause);
	}

}
