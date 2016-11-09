package com.netease.backend.db.common.proc;

public class ProcTerminateException extends ProcException {
	static final long serialVersionUID = 1L;

	public ProcTerminateException(String message) {
		super(message);
	}

	public ProcTerminateException(String message, Throwable cause) {
		super(message, cause);
	}

}
