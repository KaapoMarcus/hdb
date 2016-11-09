package com.netease.backend.db.common.exceptions;


public class ReplicationException extends Exception {
	private static final long serialVersionUID = 1L;

	
	public ReplicationException(String msg) {
		super(msg);
	}

	
	public ReplicationException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
