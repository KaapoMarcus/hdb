package com.netease.backend.db.common.exceptions;


public class SlaveUnmatchException extends Exception {

	private static final long serialVersionUID = -1661830074278638428L;

	
	public SlaveUnmatchException(String msg) {
		super(msg);
	}

	
	public SlaveUnmatchException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
