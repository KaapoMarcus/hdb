package com.netease.backend.db.common.management.dbi.memcached;


public class MemExecutionException extends Exception {

	private static final long serialVersionUID = -6501186147570202439L;

	public MemExecutionException(String msg) {
		super(msg);
	}
	
	public MemExecutionException(Throwable cause) {
		super(cause);
	}
	
	public MemExecutionException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
